package com.all.client.view.toolbar.search;

import java.util.Comparator;

import com.all.client.model.DecoratedSearchData;

public final class P2PSearchTableComparators {
	
	private P2PSearchTableComparators() {
		
	}
	
	public static Comparator<?> indexComparator(final P2PSearchTableStyle style) {
		return new Comparator<DecoratedSearchData>() {
			@Override
			public int compare(DecoratedSearchData o1, DecoratedSearchData o2) {
				boolean o1d = style.isDownloading(o1);
				boolean o2d = style.isDownloading(o2);
				if ((o1d && o2d) || !(o1d || o2d)) {
					return o1.getIndex() - o2.getIndex();
				}
				return o1d ? 1 : -1;
			}
		};
	}

	public static Comparator<?> nameComparator(P2PSearchTableStyle style) {
		return new Comparator<DecoratedSearchData>() {
			@Override
			public int compare(DecoratedSearchData o1, DecoratedSearchData o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};
	}

	public static Comparator<?> sizeComparator(P2PSearchTableStyle style) {
		return new Comparator<DecoratedSearchData>() {
			@Override
			public int compare(DecoratedSearchData o1, DecoratedSearchData o2) {
				return (int) (o1.getSize() - o2.getSize());
			}
		};
	}

	public static Comparator<?> typeComparator(P2PSearchTableStyle style) {
		return new Comparator<DecoratedSearchData>() {
			@Override
			public int compare(DecoratedSearchData o1, DecoratedSearchData o2) {
				return o1.getFileType().compareTo(o2.getFileType());
			}
		};
	}

	public static Comparator<?> peersComparator(P2PSearchTableStyle style) {
		return new Comparator<DecoratedSearchData>() {
			@Override
			public int compare(DecoratedSearchData o1, DecoratedSearchData o2) {
				return o1.getPeers() - o2.getPeers();
			}
		};
	}

}
