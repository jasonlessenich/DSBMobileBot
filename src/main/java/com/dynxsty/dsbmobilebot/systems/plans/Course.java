package com.dynxsty.dsbmobilebot.systems.plans;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public enum Course {
	BIOLOGIE("bi%s", true),
	CHEMIE("ch%s", false),
	DARSTELLENDES_SPIEL("ds%s", false),
	DEUTSCH("de%s", true),
	ENGLISCH("en%s", true),
	FRANZOESISCH("fr%s", false),
	GEOGRAFIE("gf%s", true),
	GESCHICHTE("ge%s", true),
	INFORMATIK("if%s", false),
	KUNST("ku%s", false),
	LATEIN("la%s", false),
	MATHEMATIK("ma%s", true),
	MUSIK("mu%s", false),
	PHILOSOPHIE("phi%s", false),
	PHYSIK("ph%s", true),
	POLITISCHE_BILDUNG("pb%s", true),
	RUSSISCH("ru%s", false),
	SEMINARFACH("sf%s", false),
	SPANISCH("sn%s", false),
	SPORT("sp%s", false);

	private final String min;
	private final boolean allowAdvanced;

	private boolean advanced;
	private int courseId;

	Course(String min, boolean allowAdvanced) {
		this.min = min;
		this.allowAdvanced = allowAdvanced;
	}

	public static Optional<Course> containsCourse(String line) {
		line = line.toLowerCase();
		if (line.isEmpty() || line.isBlank()) return Optional.empty();
		for (Course c : Course.values()) {
			for (int i = 1; i < 5; i++) {
				c.setCourseId(i);
				if (line.contains(c.getShort()) || line.contains(c.getNormal()) || line.contains(c.getAdvanced())) {
					c.setAdvanced(line.contains(c.getAdvanced()));
					return Optional.of(c);
				}
			}
		}
		return Optional.empty();
	}

	public static Course ofDatabaseString(String s) {
		String[] split = s.split(":");
		Course course = Course.valueOf(split[0]);
		course.setAdvanced(Boolean.parseBoolean(split[2]));
		course.setCourseId(Integer.parseInt(split[1]));
		return course;
	}

	public boolean isAdvanced() {
		return this.advanced;
	}

	public String getShort() {
		return String.format(this.min, courseId);
	}

	public String getNormal() {
		return String.format("%s (%s)", this.name().toLowerCase().replace("_", " "), courseId);
	}

	public String getAdvanced() {
		return String.format("%s-lk (%s)", this.name().toLowerCase().replace("_", " "), courseId);
	}

	public void setAdvanced(boolean advanced) {
		if (!allowAdvanced && advanced) {
			throw new IllegalStateException(this.name() + " does not allow Advanced Courses!");
		}
		this.advanced = advanced;
	}

	public String toDatabaseString() {
		return this.name() + ":" + courseId + ":" + isAdvanced();
	}

	@Override
	public String toString() {
		return StringUtils.upperCase(advanced ? getAdvanced() : getNormal());
	}

	public boolean advancedAllowed() {
		return allowAdvanced;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}
}
