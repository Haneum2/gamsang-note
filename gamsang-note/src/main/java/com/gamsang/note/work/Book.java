package com.gamsang.note.work;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "book")
public class Book {

    @Id
    @Column(name = "work_id")
    private Long workId;

    @Column(name = "isbn", length = 20)
    private String isbn;

    @Column(name = "pages")
    private Integer pages;

    protected Book() {
    }

    public Long getWorkId() { return workId; }
    public String getIsbn() { return isbn; }
    public Integer getPages() { return pages; }
}
