package xdi2.core.impl.json;

import java.io.IOException;

import com.google.gson.JsonObject;

public abstract class AbstractJSONStore implements JSONStore {

	private StringBuffer logBuffer;
	private boolean logEnabled;

	public AbstractJSONStore() {

		this.logBuffer = new StringBuffer();
	}

	@Override
	public final JsonObject load(String id) throws IOException {

		if (this.getLogEnabled()) this.logBuffer.append("load(" + id + ")");

		return this.loadInternal(id);
	}

	@Override
	public final void save(String id, JsonObject jsonObject) throws IOException {

		if (this.getLogEnabled()) this.logBuffer.append("save(" + id + "," + jsonObject.toString() + ")");

		this.saveInternal(id, jsonObject);
	}

	@Override
	public final void delete(String id) throws IOException {

		if (this.getLogEnabled()) this.logBuffer.append("delete(" + id + ")");

		this.deleteInternal(id);
	}

	public StringBuffer getLogBuffer() {

		return this.logBuffer;
	}

	public boolean getLogEnabled() {

		return this.logEnabled;
	}

	public void setLogEnabled(boolean logEnabled) {

		this.logEnabled = logEnabled;

	}

	public void resetLogBuffer() {

		this.logBuffer = new StringBuffer();
	}

	protected abstract JsonObject loadInternal(String id) throws IOException;
	protected abstract void saveInternal(String id, JsonObject jsonObject) throws IOException;
	protected abstract void deleteInternal(String id) throws IOException;
}
