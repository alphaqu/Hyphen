package net.oskarstrom.hyphen.data;

import java.util.List;

public class SerializerMethod {
	public List<ImplDetails> implDetails;
	public String name;

	public SerializerMethod(List<ImplDetails> implDetails, String name) {
		this.implDetails = implDetails;
		this.name = name;
	}
}
