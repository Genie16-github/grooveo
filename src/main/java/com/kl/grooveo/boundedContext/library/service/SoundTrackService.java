package com.kl.grooveo.boundedContext.library.service;

import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.repository.FileInfoRepository;
import com.kl.grooveo.boundedContext.member.entity.Member;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SoundTrackService {
    private final FileInfoRepository fileInfoRepository;

    private Specification<FileInfo> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<FileInfo> postRoot, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<FileInfo, Member> u1 = postRoot.join("artist", JoinType.LEFT);

                return cb.or(cb.like(postRoot.get("title"), "%" + kw + "%"),
                        cb.like(u1.get("username"), "%" + kw + "%"));
            }
        };
    }

    public Page<FileInfo> getList(String kw, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<FileInfo> spec = search(kw);
        return this.fileInfoRepository.findAll(spec, pageable);
    }

    public FileInfo getSoundTrack(Long id) {
        Optional<FileInfo> optionalFileInfo = fileInfoRepository.findById(id);
        return optionalFileInfo.orElse(null);
    }

    public void delete(FileInfo fileInfo) {
        fileInfoRepository.delete(fileInfo);
    }

    public void modify(FileInfo fileInfo, String title, String description) {
        fileInfo.setTitle(title);
        fileInfo.setModifyDate(LocalDateTime.now());
        fileInfo.setDescription(description);
        this.fileInfoRepository.save(fileInfo);
    }

    // 조회수 카운트
    @Transactional
    public int updateView(Long id) {
        return this.fileInfoRepository.updateView(id);
    }

    public int getViewCnt(Long postId) {
        Optional<FileInfo> fileInfo = fileInfoRepository.findById(postId);
        return fileInfo.map(FileInfo::getView).orElse(-1);
    }
}