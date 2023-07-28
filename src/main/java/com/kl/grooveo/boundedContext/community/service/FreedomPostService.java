package com.kl.grooveo.boundedContext.community.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kl.grooveo.base.exception.DataNotFoundException;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.community.repository.FreedomPostRepository;
import com.kl.grooveo.boundedContext.member.entity.Member;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FreedomPostService {
	private final FreedomPostRepository freedomPostRepository;

	private Specification<FreedomPost> search(String kw, Integer boardType, String category) {
    /* 검색어를 입력으로 받아, Specification 인터페이스를 구현한 객체를 반환
       Specificaion은 JPA에서 제공하는 인터페이스로, 동적 쿼리를 만드는 데 사용됨
       동적 쿼리 : 실행 시점에 쿼리 조건이나 결과를 결정하는 쿼리. 즉, 검색 조건이나 정렬 기준 등이 프로그램 실행 중에 동적으로 변경될 수 있는 경우에 사용 */
		return new Specification<>() {
			// 익명 클래스를 사용하여 Specification 인터페이스를 구현한 객체를 생성
			private static final long serialVersionUID = 1L;

			/* serialVersionUID : 자바 직렬화 프로토콜에서 사용되는 고유 식별자
								  Serializable 인터페이스를 구현한 클래스에서 사용. 이 인터페이스는 객체를 직렬화할 수 있도록 해주는데, 이때 serialVersionUID를 명시하면 클래스의 버전 정보를 명시적으로 지정할 수 있음
								  직렬화된 객체를 역직렬화(deserialization)할 때, 클래스의 버전 정보를 확인하고 호환성 여부를 판단
								  명시적으로 값을 지정하지 않으면 컴파일러가 자동으로 계산하여 부여
			   직렬화 : 자바 객체를 이진 데이터(binary data)로 변환하는 과정을 말함. 이진 데이터로 변환된 객체는 파일이나 네트워크 전송 등을 통해 저장되거나 전송할 수 있음 */
			@Override
			public Predicate toPredicate(Root<FreedomPost> postRoot, CriteriaQuery<?> query, CriteriaBuilder cb) {
            /* toPredicate : Specification 인터페이스에서 정의된 추상 메서드 중 하나로 JPA Criteria API를 사용하여 동적 쿼리를 생성하기 위해 구현해야 함
                             해당 메서드의 역할은 조건식(Predicate)를 생성하는 것으로, 조건식은 JPA Criteria API의 CriteriaQuery 인터페이스의 where() 메서드나 join() 메서드와 함께 사용
                             이 메서드는 Specification 인터페이스를 구현한 객체를 만들 때 반드시 구현해야 하는 메서드 중 하나이며, 동적 쿼리를 생성하기 위한 가장 중요한 메서드
               CriteriaQuery : JPA 쿼리를 나타내는 객체. 쿼리의 SELECT, FROM, WHERE, ORDER BY 등을 설정할 수 있음
               CriteriaBuilder : 쿼리의 WHERE 조건 등에서 사용되는 조건식을 만드는 데 사용되는 객체
               Root : 쿼리의 FROM 절에서 사용되는 엔티티의 루트(root) 객체. 루트 객체를 사용하여 엔티티의 속성에 접근할 수 있음
               => 이 매개변수들은 Specification 인터페이스에서 toPredicate() 메서드를 구현할 때 반드시 사용해야 하는 매개변수
                  메서드 내에서 동적으로 생성한 객체에 대한 매개변수 등도 사용할 수 있지만, 정해져 있는 것이 아니므로 사용하는 객체에 따라 다를 수 있음*/
				query.distinct(true);
				// 중복된 결과를 제거하도록 쿼리에 distinct 를 추가
				Join<FreedomPost, Member> u1 = postRoot.join("author", JoinType.LEFT);
                /* author 필드를 기준으로 FreedomPost 엔티티와 Member 엔티티를 조인. LEFT 조인을 사용하므로, FreedomPost 와 연관된 Member 가 없는 경우에도 결과가 반환됨
                   u1은 조인한 결과를 나타내는 객체 */

				if (category != null && category.equals("")) {
					return cb.and(cb.equal(postRoot.get("boardType"), boardType),
						cb.or(cb.like(postRoot.get("title"), "%" + kw + "%"),
							cb.like(postRoot.get("content"), "%" + kw + "%"),
							cb.like(u1.get("nickName"), "%" + kw + "%")));
				}
				return cb.and(cb.equal(postRoot.get("boardType"), boardType),
					cb.and(cb.equal(postRoot.get("category"), category),
						cb.or(cb.like(postRoot.get("title"), "%" + kw + "%"),
							cb.like(postRoot.get("content"), "%" + kw + "%"),
							cb.like(u1.get("nickName"), "%" + kw + "%"))));
                /* 검색어를 기반으로 다양한 조건을 or 연산자로 묶어서 반환
                   cb는 CritierBuider 객체로, JPA Critier API를 사용하여 쿼리를 생성할 때 사용
                   cb.like() 메서드로 각 필드에서 검색어를 포함하는 경우를 찾음 -> 각 조건은 or연산자로 묶여 있으므로, 하나 이상의 조건이 true인 경우에 해당하는 엔티티가 검색 결과로 반환 */
			}
		};
	}

	public Page<FreedomPost> getList(Integer boardType, @Nullable String category, String kw, int page) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		Specification<FreedomPost> spec = search(kw, boardType, category);
		return this.freedomPostRepository.findAll(spec, pageable);
	}

	public void create(Integer boardType, String title, String category, String content, Member author) {
		FreedomPost freedomPost = new FreedomPost();
		freedomPost.setBoardType(boardType);
		freedomPost.setTitle(title);
		freedomPost.setContent(content);
		freedomPost.setAuthor(author);
		freedomPost.setCreateDate(LocalDateTime.now());
		freedomPost.setCategory(category);
		this.freedomPostRepository.save(freedomPost);
	}

	public FreedomPost getFreedomPost(Long id) {
		return this.freedomPostRepository.findById(id).orElseThrow(
			() -> new DataNotFoundException("해당 글을 찾을 수 없습니다.")
		);
	}

	public void delete(FreedomPost community) {
		this.freedomPostRepository.delete(community);
	}

	public void modify(FreedomPost freedomPost, String title, String category, String content) {
		freedomPost.setTitle(title);
		freedomPost.setContent(content);
		freedomPost.setModifyDate(LocalDateTime.now());
		freedomPost.setCategory(category);
		this.freedomPostRepository.save(freedomPost);
	}

	// 조회수 카운트
	@Transactional
	public int updateView(Long id) {
		return this.freedomPostRepository.updateView(id);
	}

	public Page<FreedomPost> getList(Long userId, int page) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		return freedomPostRepository.findAllByAuthorId(userId, pageable);
	}

	public int getViewCnt(Long postId) {
		Optional<FreedomPost> freedomPost = freedomPostRepository.findById(postId);
		return freedomPost.map(FreedomPost::getView).orElse(-1);
	}
}