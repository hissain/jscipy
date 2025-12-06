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

import org.apache.commons.math3.complex.Complex;

/**
 * Internal design class for Chebyshev Type II filters.
 */
class Chebyshev2Design extends Cascade {

    class AnalogLowPass extends LayoutBase {

        private int nPoles;
        private double stopBandDb;

        public AnalogLowPass(int _nPoles, double _stopBandDb) {
            super(_nPoles);
            nPoles = _nPoles;
            stopBandDb = _stopBandDb;
        }

        public void design() {
            reset();
            double eps = Math.sqrt(Math.pow(10, stopBandDb / 10.0) - 1);
            
            double v0 = MathSupplement.asinh(eps) / nPoles;
            double sinh_v0 = Math.sinh(v0);
            double cosh_v0 = Math.cosh(v0);

            double n2 = 2 * nPoles;
            int pairs = nPoles / 2;
            for (int i = 0; i < pairs; ++i) {
                double k = 2 * i + 1;
                double theta = k * Math.PI / n2;
                double re = -sinh_v0 * Math.sin(theta);
                double im = cosh_v0 * Math.cos(theta);
                Complex p = new Complex(re, im);
                p = MathSupplement.recip(p);
                
                double mag = 1.0 / Math.cos(theta);
                Complex z = new Complex(0, mag);
                
                addPoleZeroConjugatePairs(p, z);
            }

            if ((nPoles & 1) == 1) {
                Complex p = new Complex(-sinh_v0);
                p = MathSupplement.recip(p);
                add(p, Complex.INF);
            }
            setNormal(0, 1);
        }
    }

    private void setupLowPass(int order, double stopBandDb, double sampleRate,
            double cutoffFrequency, int directFormType) {

        AnalogLowPass m_analogProto = new AnalogLowPass(order, stopBandDb);
        m_analogProto.design();

        LayoutBase m_digitalProto = new LayoutBase(order);

        new LowPassTransform(cutoffFrequency / sampleRate, m_digitalProto,
                m_analogProto);

        setLayout(m_digitalProto, directFormType);
    }

    public void lowPass(int order, double stopBandDb, double sampleRate, double cutoffFrequency) {
        setupLowPass(order, stopBandDb, sampleRate, cutoffFrequency,
                DirectFormAbstract.DIRECT_FORM_II);
    }

    private void setupHighPass(int order, double stopBandDb, double sampleRate,
            double cutoffFrequency, int directFormType) {

        AnalogLowPass m_analogProto = new AnalogLowPass(order, stopBandDb);
        m_analogProto.design();

        LayoutBase m_digitalProto = new LayoutBase(order);

        new HighPassTransform(cutoffFrequency / sampleRate, m_digitalProto,
                m_analogProto);

        setLayout(m_digitalProto, directFormType);
    }

    public void highPass(int order, double stopBandDb, double sampleRate, double cutoffFrequency) {
        setupHighPass(order, stopBandDb, sampleRate, cutoffFrequency,
                DirectFormAbstract.DIRECT_FORM_II);
    }

    private void setupBandStop(int order, double stopBandDb, double sampleRate,
            double centerFrequency, double widthFrequency, int directFormType) {

        AnalogLowPass m_analogProto = new AnalogLowPass(order, stopBandDb);
        m_analogProto.design();

        LayoutBase m_digitalProto = new LayoutBase(order * 2);

        new BandStopTransform(centerFrequency / sampleRate, widthFrequency
                / sampleRate, m_digitalProto, m_analogProto);

        setLayout(m_digitalProto, directFormType);
    }

    public void bandStop(int order, double stopBandDb, double sampleRate, double centerFrequency,
            double widthFrequency) {
        setupBandStop(order, stopBandDb, sampleRate, centerFrequency, widthFrequency,
                DirectFormAbstract.DIRECT_FORM_II);
    }

    private void setupBandPass(int order, double stopBandDb, double sampleRate,
            double centerFrequency, double widthFrequency, int directFormType) {

        AnalogLowPass m_analogProto = new AnalogLowPass(order, stopBandDb);
        m_analogProto.design();

        LayoutBase m_digitalProto = new LayoutBase(order * 2);

        new BandPassTransform(centerFrequency / sampleRate, widthFrequency
                / sampleRate, m_digitalProto, m_analogProto);

        setLayout(m_digitalProto, directFormType);
    }

    public void bandPass(int order, double stopBandDb, double sampleRate, double centerFrequency,
            double widthFrequency) {
        setupBandPass(order, stopBandDb, sampleRate, centerFrequency, widthFrequency,
                DirectFormAbstract.DIRECT_FORM_II);
    }
}
