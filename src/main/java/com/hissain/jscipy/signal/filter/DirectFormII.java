/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *  Copyright (c) 2009 by Vinnie Falco
 *  Copyright (c) 2016 by Bernd Porr
 */

package com.hissain.jscipy.signal.filter;

/**
 * Implementation of a Direct Form II filter with its states.
 * <p>
 * This class maintains the state (delay lines) for a Direct Form II filter
 * structure.
 * The coefficients are supplied externally via a {@link Biquad} object during
 * processing.
 */
class DirectFormII extends DirectFormAbstract {

    double m_v1; // v[-1]
    double m_v2; // v[-2]

    public DirectFormII() {
        reset();
    }

    public void reset() {
        m_v1 = 0;
        m_v2 = 0;
    }

    /**
     * Set the internal delay states.
     * For Direct Form II, v1 is w[n-1] and v2 is w[n-2].
     * 
     * @param v1 First delay state w[n-1]
     * @param v2 Second delay state w[n-2]
     */
    public void convertAndSetState(double v1, double v2) {
        m_v1 = v1;
        m_v2 = v2;
    }

    /**
     * Process a single sample through a biquad section using Direct Form II.
     *
     * @param in The input sample.
     * @param s  The biquad section containing coefficients (a1, a2, b0, b1, b2).
     * @return The filtered output sample.
     */
    public double process1(double in, Biquad s) {
        if (s != null) {
            double w = in - s.m_a1 * m_v1 - s.m_a2 * m_v2;
            double out = s.m_b0 * w + s.m_b1 * m_v1 + s.m_b2 * m_v2;

            m_v2 = m_v1;
            m_v1 = w;

            return out;
        } else {
            return in;
        }
    }
}
