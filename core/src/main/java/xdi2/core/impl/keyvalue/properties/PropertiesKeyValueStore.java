package xdi2.core.impl.keyvalue.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.impl.keyvalue.AbstractKeyValueStore;
import xdi2.core.impl.keyvalue.KeyValueStore;

/**
 * This class defines access to a properties file. It is used by the
 * PropertiesGraphFactory class to create graphs stored in properties files.
 * 
 * @author markus
 */
public class PropertiesKeyValueStore extends AbstractKeyValueStore implements KeyValueStore {

	private String path;
	private boolean autoSave;

	private Properties properties;

	public PropertiesKeyValueStore(String path, boolean autoSave) {

		this.path = path;
		this.autoSave = autoSave;

		this.properties = null;
	}

	@Override
	public void put(String key, String value) {

		String hash = hash(value);

		// find index

		String index;

		try {

			index = this.properties.getProperty(key + "___" + hash);
		} catch(Exception ex) {

			index = null;
		}

		// find index list

		String indexlist;

		try {

			indexlist = this.properties.getProperty(key + "___");
			if (indexlist.trim().equals("")) indexlist = null;
		} catch (Exception ex) {

			indexlist = null;
		}

		String[] indices = indexlist == null ? new String[0] : indexlist.trim().split(" ");

		// check if it exists

		if (Arrays.asList(indices).contains(index)) return;

		// add new content

		String newindex = UUID.randomUUID().toString();

		this.properties.setProperty(key + "___", (indexlist == null ? "" : indexlist + " ") + newindex);
		this.properties.setProperty(key + "___" + newindex, value);
		this.properties.setProperty(key + "___" + hash, newindex);

		if (this.autoSave) this.save();
	}

	@Override
	public String getOne(String key) {

		return super.getOne(key);
	}

	@Override
	public Iterator<String> getAll(String key) {

		// find index list

		String indexlist;

		try {

			indexlist = this.properties.getProperty(key + "___");
			if (indexlist.trim().equals("")) indexlist = null;
		} catch (Exception ex) {

			indexlist = null;
		}

		String[] indices = indexlist == null ? new String[0] : indexlist.trim().split(" ");

		// find contents

		List<String> contents = new ArrayList<String> (indices.length);

		for (int i=0; i<indices.length; i++) {

			String content = this.properties.getProperty(key + "___" + indices[i]);
			if (content == null) continue;

			contents.add(content);
		}

		// done

		return contents.iterator();
	}

	@Override
	public boolean contains(String key) {

		// find index list

		String indexlist;

		try {

			indexlist = this.properties.getProperty(key + "___");
			if (indexlist.trim().equals("")) indexlist = null;
		} catch (Exception ex) {

			indexlist = null;
		}

		// done

		return indexlist != null;
	}

	@Override
	public boolean contains(String key, String value) {

		String hash = hash(value);

		// find index

		String index;

		try {

			index = this.properties.getProperty(key + "___" + hash);
		} catch(Exception ex) {

			index = null;
		}

		// find index list

		String indexlist;

		try {

			indexlist = this.properties.getProperty(key + "___");
			if (indexlist.trim().equals("")) indexlist = null;
		} catch (Exception ex) {

			indexlist = null;
		}

		String[] indices = indexlist == null ? new String[0] : indexlist.trim().split(" ");

		// done

		return Arrays.asList(indices).contains(index);
	}

	@Override
	public void delete(String key) {

		this.properties.remove(key + "___");

		if (this.autoSave) this.save();
	}

	@Override
	public void delete(String key, String value) {

		String hash = hash(value);

		// find index

		String index;

		try {

			index = this.properties.getProperty(key + "___" + hash);
		} catch(Exception ex) {

			index = null;
		}

		if (index == null) return;

		// find index list

		String indexlist;

		try {

			indexlist = this.properties.getProperty(key + "___");
			if (indexlist.trim().equals("")) indexlist = null;
		} catch (Exception ex) {

			indexlist = null;
		}

		String[] indices = indexlist == null ? new String[0] : indexlist.trim().split(" ");

		// create new index list

		String newindexlist = "";

		for (int i=0; i<indices.length; i++) {

			if (indices[i].equals(index)) continue;
			newindexlist += " " + indices[i];
		}

		// store new index list

		this.properties.setProperty(key + "___", newindexlist);
		this.properties.remove(key + "___" + index);
		this.properties.remove(key + "___" + hash);

		if (this.autoSave) this.save();
	}

	@Override
	public void clear() {

		this.properties.clear();

		if (this.autoSave) this.save();
	}

	@Override
	public void close() {

		this.save();

		this.path = null;
		this.properties = null;
	}

	@Override
	public void beginTransaction() {

		if (this.properties == null) this.load();
	}

	@Override
	public void commitTransaction() {

		this.save();
	}

	@Override
	public void rollbackTransaction() {

		this.load();
	}

	public void load() {

		try {

			File file = new File(this.path);

			if (! file.exists()) file.createNewFile();

			this.properties = new Properties();
			this.properties.load(new FileInputStream(this.path));
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Cannot load properties file: " + ex.getMessage(), ex);
		}
	}

	public void save() {

		try {

			OutputStream stream = new FileOutputStream(this.path);

			this.properties.store(stream, null);
			stream.close();
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Cannot save properties file: " + ex.getMessage(), ex);
		}
	}

	private static String hash(String str) {

		String hash;

		try {

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			digest.update(str.getBytes());
			hash = new String(Base64.encodeBase64(digest.digest()), "UTF-8");
		} catch (Exception ex) {

			throw new RuntimeException("hash(): " + ex.getMessage(), ex);
		}

		return hash;
	}
}
