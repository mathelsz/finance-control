package com.matt.financial.model.service;

import com.matt.financial.config.FinancialBusinessException;
import com.matt.financial.model.entity.Subject;
import com.matt.financial.model.repository.SubjectRepository;
import com.matt.financial.model.specification.SubjectSpecification;
import com.matt.financial.validations.SubjectValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SubjectService {

    private final SubjectSpecification subjectSpecification;
    private final SubjectRepository subjectRepository;
    private final SubjectValidator subjectValidator;

    @Autowired
    public SubjectService(SubjectSpecification subjectSpecification,SubjectRepository subjectRepository,
                          SubjectValidator subjectValidator) {
        this.subjectSpecification = subjectSpecification;
        this.subjectRepository = subjectRepository;
        this.subjectValidator = subjectValidator;
    }

    @Transactional(readOnly = true)
    public Page<Subject> findAll(Subject subject, Pageable pageable) {
        return this.subjectRepository.findAll(
                this.subjectSpecification.filter(subject),
                pageable
        );
    }

    @Transactional(readOnly = true)
    public Subject findById(UUID subjectId) {
        return this.subjectRepository.findById(subjectId)
                .orElseThrow(() -> new FinancialBusinessException("Subject not found"));
    }

    @Transactional(readOnly = true)
    public Subject findByUsername(String username) {
        return (Subject) this.subjectRepository.findSubjectByUsername(username);
    }

    private Subject save(Subject subject) {
        return this.subjectRepository.save(subject);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Subject create(Subject subject) {
        this.subjectValidator.existsByUsername(subject.getUsername());

        return this.save(subject);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Subject update(Subject subject) {
        var subjectToUpdate = this.findById(subject.getId());

        subjectToUpdate.mergeForUpdate(subject);

        return this.save(subjectToUpdate);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Subject updateToUsername(Subject subject, String newUsername) {
        var subjectToUpdate = this.findById(subject.getId());

        this.subjectValidator.existsByUsername(newUsername);

        subjectToUpdate.setUsername(newUsername);

        return this.save(subjectToUpdate);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Subject updateToEmail(Subject subject, String newEmail) {
        var subjectToUpdate = this.findById(subject.getId());

        this.subjectValidator.existsByEmail(newEmail);

        subjectToUpdate.setEmail(newEmail);

        return this.save(subjectToUpdate);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean activateOrDeactivate(UUID subjectId) {
        var subjectToUpdate = this.findById(subjectId);

        subjectToUpdate.setActive(!subjectToUpdate.getActive());

        return this.save(subjectToUpdate).getActive();
    }
}
