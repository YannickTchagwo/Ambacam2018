package com.ambacam.transfert;

import java.util.ArrayList;
import java.util.List;

public class SearchResultTO<T> {

	private int page;

	private int totalPages;

	private List<T> content = new ArrayList<>();

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

}
