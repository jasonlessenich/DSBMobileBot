package com.dynxsty.dsbmobilebot.systems.plans;

import com.dynxsty.dsbmobilebot.util.Pair;

public enum Course {
	MATHEMATIK("ma%s"),
	DEUTSCH("de%s"),
	ENGLISCH("en%s"),
	SPANISCH("sn%s"),
	FRANZOESISCH("fr%s"),
	RUSSISCH("ru%s"),
	LATEIN("la%s"),
	BIOLOGIE("bi%s"),
	CHEMIE("ch%s"),
	PHYSIK("ph%s"),
	GEOGRAFIE("gf%s"),
	POLITISCHE_BILDUNG("pb%s"),
	INFORMATIK("if%s"),
	DARSTELLENDES_SPIEL("ds%s"),
	GESCHICHTE("ge%s"),
	PHILOSOPHIE("phi%s"),
	KUNST("ku%s"),
	MUSIK("mu%s"),
	SPORT("sp%s"),
	SEMINARFACH("sf%s")
	;

	private final String min;

	Course(String min) {
		this.min = min;
	}

	public String getMin(int course) {
		return String.format(this.min, course);
	}

	public String getNormal(int course) {
		return String.format("%s (%s)", this.name().toLowerCase().replace("_", " "), course);
	}

	public String getAdvanced(int course) {
		return String.format("%s-lk (%s)", this.name().toLowerCase().replace("_", " "), course);
	}

	public static boolean matches(String s, int course) {
		for (Course value : Course.values()) {
			s = s.toLowerCase();
			if (s.contains(value.getMin(course)) || s.contains(value.getNormal(course)) ||
					s.contains(value.getAdvanced(course))) {
				return true;
			}
		}
		return false;
	}

	public String toString(int course) {
		return this.name() + ":" + course;
	}

	public Pair<Course, Integer> ofString(String s) {
		String[] split = s.split(":");
		return new Pair<>(Course.valueOf(split[0]), Integer.valueOf(split[1]));
	}
}
