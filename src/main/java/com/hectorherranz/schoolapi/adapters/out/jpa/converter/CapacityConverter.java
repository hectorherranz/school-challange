package com.hectorherranz.schoolapi.adapters.out.jpa.converter;

import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CapacityConverter implements AttributeConverter<Capacity, Integer> {
    @Override public Integer convertToDatabaseColumn(Capacity attr) { return attr == null ? null : attr.value(); }
    @Override public Capacity convertToEntityAttribute(Integer db)   { return db == null ? null : new Capacity(db); }
}
