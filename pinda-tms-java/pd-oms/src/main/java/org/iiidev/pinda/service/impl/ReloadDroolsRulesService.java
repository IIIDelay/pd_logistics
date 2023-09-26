package org.iiidev.pinda.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.entity.Rule;
import org.iiidev.pinda.mapper.RuleMapper;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 重新加载规则
 */
@Slf4j
@Service
public class ReloadDroolsRulesService {
    public static KieContainer kieContainer;

    @Autowired
    private RuleMapper ruleMapper;

    public void reload() {
        KieContainer kieContainer = loadContainerFromString(loadRules());
        this.kieContainer = kieContainer;
    }

    private List<Rule> loadRules() {
        List<Rule> rules = ruleMapper.selectList(null);
        return rules;
    }

    public KieContainer loadContainerFromString(List<Rule> rules) {
        long startTime = System.currentTimeMillis();
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();

        for (Rule rule : rules) {
            String drl = rule.getContent();
            kfs.write("src/main/resources/" + drl.hashCode() + ".drl", drl);
        }

        KieBuilder kb = ks.newKieBuilder(kfs);

        kb.buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }
        long endTime = System.currentTimeMillis();
        log.info("Time to build rules: {} ms", (endTime - startTime));
        startTime = System.currentTimeMillis();
        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
        endTime = System.currentTimeMillis();
        log.info("Time to load container: {} ms", (endTime - startTime));
        return kContainer;
    }
}