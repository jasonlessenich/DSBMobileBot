package com.dynxsty.dsbmobilebot.systems.plans.commands.notification.dao;

import com.dynxsty.dsbmobilebot.systems.plans.Course;
import com.dynxsty.dsbmobilebot.systems.plans.commands.notification.model.NotificationAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class NotificationAccountRepository {
	private final Connection con;

	/**
	 * Inserts a new {@link NotificationAccount}.
	 *
	 * @param account The account to insert.
	 * @throws SQLException If an error occurs.
	 */
	public void insert(NotificationAccount account) throws SQLException {
		try (PreparedStatement s = con.prepareStatement("INSERT INTO guild_notification_account (user_id, class, subjects) VALUES ( ?, ?, ? )")) {
			s.setLong(1, account.getUserId());
			s.setInt(2, account.getClassLevel());
			s.setArray(3, con.createArrayOf("VARCHAR", account.getSubjects()));
			s.executeUpdate();
			log.info("Inserted new GuildNotificationAccount: " + account);
		}
	}

	/**
	 * Updates a single {@link NotificationAccount}.
	 *
	 * @param account The account to update.
	 * @throws SQLException If an error occurs.
	 */
	public void update(NotificationAccount account) throws SQLException {
		try (PreparedStatement s = con.prepareStatement("UPDATE guild_notification_account SET class = ?, subjects = ? WHERE user_id = ?")) {
			s.setInt(1, account.getClassLevel());
			s.setArray(2, con.createArrayOf("VARCHAR", account.getSubjects()));
			s.setLong(3, account.getUserId());
			s.executeUpdate();
		}
	}

	/**
	 * Attempts to retrieve a {@link NotificationAccount} based on the given user id.
	 *
	 * @param userId The user's id.
	 * @return The {@link NotificationAccount} as an {@link Optional}.
	 * @throws SQLException If an error occurs.
	 */
	public Optional<NotificationAccount> getByUserId(long userId) throws SQLException {
		try (PreparedStatement s = con.prepareStatement("SELECT * FROM guild_notification_account WHERE user_id = ?")) {
			s.setLong(1, userId);
			ResultSet rs = s.executeQuery();
			NotificationAccount account = null;
			if (rs.next()) {
				account = this.read(rs);
			}
			return Optional.ofNullable(account);
		}
	}

	public List<NotificationAccount> getBySubject(Course course) throws SQLException{
		try (PreparedStatement s = con.prepareStatement("SELECT * FROM guild_notification_account WHERE ARRAY_CONTAINS(subjects, ?)")) {
			s.setString(1, course.toDatabaseString());
			ResultSet rs = s.executeQuery();
			List<NotificationAccount> accounts = new ArrayList<>();
			while (rs.next()) {
				accounts.add(read(rs));
			}
			return accounts;
		}
	}

	private NotificationAccount read(ResultSet rs) throws SQLException {
		NotificationAccount account = new NotificationAccount();
		account.setUserId(rs.getLong("user_id"));
		account.setClassLevel(rs.getInt("class"));
		account.setSubjects(convertArrayToStringArray(rs.getArray("subjects")));
		return account;
	}

	private String[] convertArrayToStringArray(Array array) throws SQLException {
		Object[] tmp = (Object[]) array.getArray();
		String[] stringArray = new String[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			stringArray[i] = (String) tmp[i];
		}
		return stringArray;
	}
}
