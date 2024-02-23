/**
 * Copyright (C) 2020-2024 Dominik Glaser
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.dogla.toaster;

import lombok.Getter;

/**
 * Instances of this class are descriptions of colors in terms of the primary
 * additive color model (red, green and blue). A color may be described in terms
 * of the relative intensities of these three primary colors. The brightness of
 * each color is specified by a value in the range 0 to 255, where 0 indicates
 * no color (blackness) and 255 indicates maximum intensity.
 * <p>
 * The hashCode() method in this class uses the values of the public fields to
 * compute the hash value. When storing instances of the class in hashed
 * collections, do not modify these fields after the object has been inserted.
 * </p>
 *
 * @author Dominik
 */
@Getter
public final class ToastColor {

    private static final double FACTOR = 0.7;

	private int red;
	private int green;
	private int blue;

	/**
	 * Constructs an instance of this class with the given red, green and blue values.
	 *
	 * @param red
	 *            the red component of the new instance
	 * @param green
	 *            the green component of the new instance
	 * @param blue
	 *            the blue component of the new instance
	 *
	 * @exception IllegalArgumentException if the red, green or blue argument is not between 0 and 255
	 */
	public ToastColor(int red, int green, int blue) {
		if ((red > 255) || (red < 0) || (green > 255) || (green < 0) || (blue > 255) || (blue < 0)) {
			throw new IllegalArgumentException("Values have to be between 0 to 255"); //$NON-NLS-1$
		}
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
    /**
     * Creates a new <code>ToastColor</code> that is a brighter version of this <code>ToastColor</code>.
     * <p>
     * This method applies an arbitrary scale factor to each of the three RGB
     * components of this <code>ToastColor</code> to create a brighter version of this <code>ToastColor</code>.
     * Although <code>brighter</code> and <code>darker</code> are inverse operations, the results of a
     * series of invocations of these two methods might be inconsistent because of rounding errors.
     * 
     * @return a new <code>ToastColor</code> object that is a brighter version of this <code>ToastColor</code>.
     * @see #darker()
     */
    public ToastColor brighter() {
        int r = red;
        int g = green;
        int b = blue;

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int)(1.0/(1.0-FACTOR));
        if ( r == 0 && g == 0 && b == 0) {
            return new ToastColor(i, i, i);
        }
        if ( r > 0 && r < i ) {
			r = i;
		}
        if ( g > 0 && g < i ) {
			g = i;
		}
        if ( b > 0 && b < i ) {
			b = i;
		}

        return new ToastColor(Math.min((int)(r/FACTOR), 255), Math.min((int)(g/FACTOR), 255), Math.min((int)(b/FACTOR), 255));
    }

    /**
     * Creates a new <code>ToastColor</code> that is a darker version of this <code>ToastColor</code>.
     * <p>
     * This method applies an arbitrary scale factor to each of the three RGB
     * components of this <code>ToastColor</code> to create a darker version of
     * this <code>ToastColor</code>.
     * Although <code>brighter</code> and
     * <code>darker</code> are inverse operations, the results of a series
     * of invocations of these two methods might be inconsistent because
     * of rounding errors.
     * 
     * @return a new <code>ToastColor</code> object that is a darker version of this <code>ToastColor</code>.
     * @see #brighter()
     */
    public ToastColor darker() {
        return new ToastColor(Math.max((int)(red  *FACTOR), 0), Math.max((int)(green*FACTOR), 0), Math.max((int)(blue *FACTOR), 0));
    }
    
	/**
	 * @return a string representation of the <code>ToastColor</code> in hex format
	 */
	public String toHEX() {
		return ("#" + String.format("%02X", red) + String.format("%02X", green) + String.format("%02X", blue)).toUpperCase(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof ToastColor)) {
			return false;
		}
		ToastColor color = (ToastColor) object;
		return (color.red == this.red) && (color.green == this.green) && (color.blue == this.blue);
	}

	@Override
	public int hashCode() {
		return (blue << 16) | (green << 8) | red;
	}

	@Override
	public String toString() {
		return toHEX();
	}

}
