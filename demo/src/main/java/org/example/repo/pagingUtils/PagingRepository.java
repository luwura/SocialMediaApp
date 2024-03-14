package org.example.repo.pagingUtils;


import org.example.repo.Repo;
import org.example.domain.Entity;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repo<ID, E> {

    Page<E> getAll(Pageable pageable);
}
