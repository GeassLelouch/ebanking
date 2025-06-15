package ebanking.dto;

import java.math.BigDecimal;
import java.util.List;

public class PagedResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
//    private BigDecimal sumAmount;
    private BigDecimal sumAmountInBaseCurrency;

    public PagedResponse() {
    }

//    public PagedResponse(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last, BigDecimal sumAmount, BigDecimal sumAmountInBaseCurrency) {
    public PagedResponse(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last, BigDecimal sumAmountInBaseCurrency) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
//        this.sumAmount = sumAmount;
        this.sumAmountInBaseCurrency = sumAmountInBaseCurrency;
    }

    // -------- Getters & Setters --------

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

//	public BigDecimal getSumAmount() {
//		return sumAmount;
//	}
//
//	public void setSumAmount(BigDecimal sumAmount) {
//		this.sumAmount = sumAmount;
//	}

	public BigDecimal getSumAmountInBaseCurrency() {
		return sumAmountInBaseCurrency;
	}

	public void setSumAmountInBaseCurrency(BigDecimal sumAmountInBaseCurrency) {
		this.sumAmountInBaseCurrency = sumAmountInBaseCurrency;
	}

}
