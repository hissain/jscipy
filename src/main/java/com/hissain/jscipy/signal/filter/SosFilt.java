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
 */

package com.hissain.jscipy.signal.filter;

/**
 * Helper class to expose SOS filtering functionality while keeping
 * implementation details (SOSCascade) package-private.
 */
public class SosFilt {

    /**
     * Filter data using a series of second-order sections (SOS).
     *
     * @param signal The input signal.
     * @param sos    Array of second-order sections of shape [n_sections][6].
     *               Each section includes [b0, b1, b2, a0, a1, a2].
     * @return The filtered signal.
     */
    public static double[] sosfilt(double[] signal, double[][] sos) {
        SOSCascade filter = new SOSCascade();
        filter.setup(sos);
        double[] output = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            output[i] = filter.filter(signal[i]);
        }
        return output;
    }
}
