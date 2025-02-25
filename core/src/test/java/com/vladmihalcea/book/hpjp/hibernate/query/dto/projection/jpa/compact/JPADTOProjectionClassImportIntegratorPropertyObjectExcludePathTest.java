package com.vladmihalcea.book.hpjp.hibernate.query.dto.projection.jpa.compact;

import com.vladmihalcea.book.hpjp.hibernate.forum.dto.PostDTO;
import com.vladmihalcea.book.hpjp.hibernate.query.dto.projection.Post;
import com.vladmihalcea.book.hpjp.hibernate.query.dto.projection.PostComment;
import com.vladmihalcea.book.hpjp.util.AbstractTest;
import io.hypersistence.utils.hibernate.type.util.ClassImportIntegrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * @author Vlad Mihalcea
 */
public class JPADTOProjectionClassImportIntegratorPropertyObjectExcludePathTest extends AbstractTest {

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[]{
            Post.class,
            PostComment.class
        };
    }

    @Override
    protected void additionalProperties(Properties properties) {
        properties.put(
            "hibernate.integrator_provider",
            (IntegratorProvider) () -> List.of(
                new ClassImportIntegrator(
                    List.of(
                        PostDTO.class
                    )
                )
                .excludePath("com.vladmihalcea.book.hpjp.hibernate")
            )
        );
    }

    @Override
    public void afterInit() {
        doInJPA(entityManager -> {
            entityManager.persist(
                new Post()
                    .setId(1L)
                    .setTitle("High-Performance Java Persistence")
                    .setCreatedBy("Vlad Mihalcea")
                    .setCreatedOn(
                        LocalDateTime.of(2016, 11, 2, 12, 0, 0)
                    )
                    .setUpdatedBy("Vlad Mihalcea")
                    .setUpdatedOn(
                        LocalDateTime.now()
                    )
            );
        });
    }

    @Test
    public void testConstructorExpression() {
        doInJPA(entityManager -> {
            List<PostDTO> postDTOs = entityManager.createQuery("""
                select new forum.dto.PostDTO(
                    p.id,
                    p.title
                )
                from Post p
                where p.createdOn > :fromTimestamp
                """, PostDTO.class)
            .setParameter(
                "fromTimestamp",
                LocalDate.of(2016, 1, 1).atStartOfDay()
            )
            .getResultList();

            assertEquals(1, postDTOs.size());

            PostDTO postDTO = postDTOs.get(0);
            assertEquals(1L, postDTO.getId().longValue());
            assertEquals("High-Performance Java Persistence", postDTO.getTitle());
        });
    }
}
