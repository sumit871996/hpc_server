package com.app.entities.UseCases;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UseCasesEnumBean {

    private final Map<Integer, UseCasesEnum> useCaseIdToEnumMap = new HashMap<>();

    @jakarta.annotation.PostConstruct
    public void init() {
        for (UseCasesEnum useCase : UseCasesEnum.values()) {
            useCaseIdToEnumMap.put(useCase.getUseCaseId(), useCase);
        }
    }

    public UseCasesEnum getByUseCaseId(int useCaseId) {
        return useCaseIdToEnumMap.get(useCaseId);
    }

    public Map<Integer, UseCasesEnum> getUseCaseIdToEnumMap() {
        return useCaseIdToEnumMap;
    }
}
