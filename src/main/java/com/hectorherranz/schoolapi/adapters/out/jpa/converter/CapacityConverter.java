package com.hectorherranz.schoolapi.adapters.out.jpa.converter;

import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CapacityConverter implements AttributeConverter<Capacity, Integer> {

  @Override
  public Integer convertToDatabaseColumn(Capacity capacity) {
    return capacity != null ? capacity.value() : null;
  }

  @Override
  public Capacity convertToEntityAttribute(Integer dbData) {
    return dbData != null ? new Capacity(dbData) : null;
  }
}
