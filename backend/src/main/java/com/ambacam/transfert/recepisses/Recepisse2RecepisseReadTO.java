package com.ambacam.transfert.recepisses;

import com.ambacam.model.Recepisse;

public class Recepisse2RecepisseReadTO {
	public static RecepisseReadTO apply(Recepisse in) {
		RecepisseReadTO out = new RecepisseReadTO();
		out.setId(in.getId());
		out.setNumero(in.getNumero());
		return out;
	}
}
