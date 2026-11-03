package com.sixlv6.backend.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Instant ↔ ISO-8601 文本。
 *
 * <p>SQLite 没有原生时间类型，默认的 JDBC Timestamp 绑定/读取依赖字符串格式，
 * Hibernate 写入的 ISO 文本（带 T/Z）sqlite-jdbc 读不回来，会报 "Error parsing time stamp"。
 * 统一交给转换器处理，数据库里存的就是可读、可比较（字典序=时间序）的 UTC 文本。
 */
@Converter(autoApply = false)
public class InstantTextConverter implements AttributeConverter<Instant, String> {

    private static final DateTimeFormatter SQLITE_COMPAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String convertToDatabaseColumn(Instant attribute) {
        return attribute == null ? null : DateTimeFormatter.ISO_INSTANT.format(attribute.truncatedTo(ChronoUnit.MILLIS));
    }

    @Override
    public Instant convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        String value = dbData.trim();
        // 兼容 Flyway 里 datetime('now') 写出的 "yyyy-MM-dd HH:mm:ss"（UTC）
        if (value.indexOf('T') < 0) {
            return LocalDateTime.parse(value, SQLITE_COMPAT).toInstant(ZoneOffset.UTC);
        }
        return Instant.parse(value);
    }
}
