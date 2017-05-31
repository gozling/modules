package com.gaozl.trans;

/**
 * 
 * 
 * @author gozling
 *
 */
public abstract class Converter<A, B> {

	@SuppressWarnings("unused")
	private final boolean handleNullAutomatically;

	/** Constructor for use by subclasses. */
	protected Converter() {
		this(true);
	}

	/**
	 * Constructor used only by {@code LegacyConverter} to suspend automatic
	 * null-handling.
	 */
	Converter(boolean handleNullAutomatically) {
		this.handleNullAutomatically = handleNullAutomatically;
	}

	static <T> T checkNotNull(T obj, String message) {
		if (obj == null) {
			throw new NullPointerException(message);
		}
		return obj;
	}

	// SPI methods (what subclasses must implement)

	/**
	 * Returns a representation of {@code a} as an instance of type {@code B}.
	 * If {@code a} cannot be converted, an unchecked exception (such as
	 * {@link IllegalArgumentException}) should be thrown.
	 *
	 * @param a
	 *            the instance to convert; will never be null
	 * @return the converted instance; <b>must not</b> be null
	 */
	protected abstract B doForward(A a);

	/**
	 * Returns a representation of {@code b} as an instance of type {@code A}.
	 * If {@code b} cannot be converted, an unchecked exception (such as
	 * {@link IllegalArgumentException}) should be thrown.
	 *
	 * @param b
	 *            the instance to convert; will never be null
	 * @return the converted instance; <b>must not</b> be null
	 * @throws UnsupportedOperationException
	 *             if backward conversion is not implemented; this should be
	 *             very rare. Note that if backward conversion is not only
	 *             unimplemented but unimplement<i>able</i> (for example,
	 *             consider a {@code Converter<Chicken, ChickenNugget>}), then
	 *             this is not logically a {@code Converter} at all, and should
	 *             just implement {@link Function}.
	 */
	protected abstract A doBackward(B b);

}
