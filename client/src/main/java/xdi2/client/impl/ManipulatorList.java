package xdi2.client.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import xdi2.client.manipulator.Manipulator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingClassIterator;

public class ManipulatorList implements Iterable<Manipulator>, Serializable {

	private static final long serialVersionUID = 7867231352680332519L;

	private List<Manipulator> manipulators;

	public ManipulatorList(Collection<Manipulator> manipulators) {

		this.manipulators = new ArrayList<Manipulator> (manipulators);
	}

	public ManipulatorList(ManipulatorList manipulatorList) {

		this.manipulators = new ArrayList<Manipulator> (manipulatorList.manipulators);
	}

	public ManipulatorList() {

		this.manipulators = new ArrayList<Manipulator> ();
	}

	public void addManipulator(Manipulator manipulator) {

		this.manipulators.add(manipulator);
	}

	public void addManipulators(Collection<Manipulator> manipulators) {

		this.manipulators.addAll(manipulators);
	}

	public void addManipulators(Manipulator... manipulators) {

		this.manipulators.addAll(Arrays.asList(manipulators));
	}

	@SuppressWarnings("unchecked")
	public <T extends Manipulator> T getManipulator(Class<T> clazz) {

		for (Manipulator manipulator : this.manipulators) {

			if (clazz.isAssignableFrom(manipulator.getClass())) return (T) manipulator;
		}

		return null;
	}

	public void removeManipulator(Manipulator manipulator) {

		this.manipulators.remove(manipulator);
	}

	public boolean isEmpty() {

		return this.manipulators.isEmpty();
	}

	public int size() {

		return this.manipulators.size();
	}

	@Override
	public ReadOnlyIterator<Manipulator> iterator() {

		return new ReadOnlyIterator<Manipulator> (this.manipulators.iterator());
	}

	public String stringList() {

		StringBuffer buffer = new StringBuffer();

		for (Manipulator manipulator : this.manipulators) {

			if (buffer.length() > 0) buffer.append(",");
			buffer.append(manipulator.getClass().getSimpleName());
		}

		return buffer.toString();
	}

	public <T> Iterator<T> findManipulators(Class<T> clazz) {

		return new SelectingClassIterator<Manipulator, T> (this.iterator(), clazz);
	}

	public <T> T findManipulator(Class<T> clazz) {

		Iterator<T> manipulators = findManipulators(clazz);
		if (! manipulators.hasNext()) return null;

		return manipulators.next();
	}
}
