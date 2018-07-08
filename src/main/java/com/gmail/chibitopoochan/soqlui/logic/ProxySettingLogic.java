package com.gmail.chibitopoochan.soqlui.logic;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import com.gmail.chibitopoochan.soqlui.config.ProxySettingSet;
import com.gmail.chibitopoochan.soqlui.model.ProxySetting;

public class ProxySettingLogic {
	private ProxySetting setting;

	public ProxySettingLogic() {
		ProxySettingSet proxySetting = ProxySettingSet.getInstance();
		setting = proxySetting.getProxySetting();
	}

	public ProxySetting getProxySetting() {
		return setting;
	}

	public void setProxySetting(ProxySetting setting) {
		this.setting = setting;
	}

	public void storeSetting() {
		ProxySettingSet proxySetting = ProxySettingSet.getInstance();
		proxySetting.setProxySetting(setting);
		try {
			proxySetting.storeSetting();
		} catch (IllegalStateException | XMLStreamException | IOException e) {

		}
	}

}
