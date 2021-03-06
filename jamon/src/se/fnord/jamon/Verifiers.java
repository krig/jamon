package se.fnord.jamon;

/**
 * Utility to verify parse trees
 */
public class Verifiers {
	/**
	 * Thrown by verifiers.
	 * @param msg Information about failure
	 */
	public static class Fail extends Exception {
		public Fail(String msg) {
			super(msg);
		}
	}

	/**
	 * A node structure verifier
	 */
	public interface Verifier {
		void verify(Node n) throws Fail;
	}

	public static void test(boolean tst, final String context) throws Fail {
		if (!tst)
			throw new Fail(context);
	}

	/**
	 * Verifies the number of children
	 * @param count The number of children
	 * @return the constructed verifier
	 */
	public static Verifier childCount(final int count) {
		return new Verifier() {
			@Override
			public void verify(Node n) throws Fail {
				test(count == n.children().size(), "childCount: " + n + " != " + count);
			}
		};
	}

	/**
	 * Verifies all children against the supplied verifier
	 * @param verifier The verifier to run for each child
	 * @return the constructed verifier
	 */
	public static Verifier forEachChild(final Verifier verifier) {
		return new Verifier() {
			@Override
			public void verify(Node n) throws Fail {
				for (Node c : n.children())
					verifier.verify(c);
			}
		};
	}

	/**
	 * Verifies the node attachment
	 * @param o The object to check for equality with the node attachment
	 * @return the constructed verifier
	 */
	public static Verifier attachment(final Object o) {
		return new Verifier() {
			@Override
			public void verify(Node n) throws Fail {
				test(o == n.attachment() || o == null || o.equals(n.attachment()), "attachment: " + o + " != " + n.attachment());
			}
		};
	}

	/**
	 * Verifies the node value
	 * @param o The object to check for equality with the node value
	 * @return the constructed verifier
	 */
	public static Verifier value(final Object o) {
		return new Verifier() {
			@Override
			public void verify(Node n) throws Fail {
				test(o == n.value() || o == null || o.equals(n.value()), "value: " + n.value() + " != " + o);
			}
		};
	}

	/**
	 * Verifies the child nodes. The number of children must be equal to the number of supplied verifiers.
	 * @param verifiers The verifiers to use when verifying children
	 * @return the constructed verifier
	 */
	public static Verifier children(final Verifier... verifiers) {
		return new Verifier() {
			@Override
			public void verify(Node n) throws Fail {
				test(verifiers.length == n.children().size(), "children: " + n + " != " + verifiers.length);
				int i = 0;
				for (Node c : n.children())
					verifiers[i++].verify(c);
			}
		};
	}

	/**
	 * The conjunction of zero or more verifiers. Passing no verifiers creates a verifier that always return true.
	 * @param verifiers The verifiers whose results should be and:ed together
	 * @return the constructed verifier
	 */
	public static Verifier and(final Verifier... verifiers) {
		return new Verifier() {
			@Override
			public void verify(Node n) throws Fail {
				for (Verifier v : verifiers)
					v.verify(n);
			}
		};
	}

	/**
	 * The disjunction of zero or more verifiers. Passing no verifiers creates a verifier that always return false.
	 * @param verifiers The verifiers whose results should be or:ed together
	 * @return the constructed verifier
	 */
	public static Verifier or(final Verifier... verifiers) {
		return new Verifier() {
			@Override
			public void verify(Node n) throws Fail {
				for (Verifier v : verifiers)
					v.verify(n);
			}
		};
	}

	/**
	 * Verifies the attachment, value and children of a node.
	 * Equivalent to:
	 * <p>
	 * <code>and(attachment(attachment), value(value), children(children))</code>
	 * <p>
	 * @param attachment The object to check for equality with the node attachment
	 * @param value The object to check for equality with the node value
	 * @param verifiers The verifiers to use when verifying children
	 * @return the constructed verifier
	 */
	public static Verifier node(final Object attachment, final Object value, final Verifier... children) {
		return and(attachment(attachment), value(value), children(children));
	}

	/**
	 * Verifies that a node is empty, i.e. has no value, attachment or children
	 * @return the constructed verifier
	 */
	public static Verifier empty() {
		return and(value(null), attachment(null), childCount(0));
	}
}