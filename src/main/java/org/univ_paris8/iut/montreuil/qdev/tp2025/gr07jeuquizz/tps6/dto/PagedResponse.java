package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.dto;

import java.util.List;

/**
 * DTO de réponse paginée générique.
 *
 * @param <T> Type des éléments de la liste
 */
public class PagedResponse<T> {

    private List<T> data;
    private int page;
    private int pageSize;
    private long total;
    private int totalPages;

    public PagedResponse() {
    }

    public PagedResponse(List<T> data, int page, int pageSize, long total) {
        this.data = data;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
