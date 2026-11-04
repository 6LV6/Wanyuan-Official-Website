package com.sixlv6.backend.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

@Converter
public class InstantTextConverter implements AttributeConverter<Instant,String>{
    /**兼容 SQLite / Flyway 的 datetime('now')，形如 2026-11-03 08:30:00（UTC） */
    private static final DateTimeFormatter SQLITE_TEXT_FORMAT=
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String convertToDatabaseColumn(Instant attribute){
        if(attribute==null){
            return null;
        }
        return DateTimeFormatter.ISO_INSTANT.format(attribute.truncatedTo(ChronoUnit.MILLIS));
    }

    @Override
    public Instant convertToEntityAttribute(String dbData){
        if(dbData==null||dbData.isBlank()){
            return null;
        }
        String value=dbData.trim();

        //1)优先按 ISO_INSTANT 解析，例如 2026-11-03T08:30:00.123Z
        try{
            return Instant.parse(value);
        }catch (DateTimeParseException ignored){
            // 落到下面的兼容分支
        }
        //2)兼容 "yyyy-MM-dd HH:mm:ss"，Flyway 的 datetime('now') 就是这个格式，按 UTC 处理
        LocalDateTime parsed=LocalDateTime.parse(value,SQLITE_TEXT_FORMAT);
        return parsed.toInstant(ZoneOffset.UTC);
    }
}