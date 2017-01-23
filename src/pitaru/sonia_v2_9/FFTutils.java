/* FFTutils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package pitaru.sonia_v2_9;

class FFTutils
{
    float HALF_PI = 1.5707964F;
    float TWO_PI = 6.2831855F;
    int WINDOW_SIZE;
    int WS2;
    int BIT_LEN;
    int[] _bitrevtable;
    float _normF;
    float[] _equalize;
    float[] _envelope;
    float[] _fft_result;
    float[][] _fftBuffer;
    float[] _cosLUT;
    float[] _sinLUT;
    float[] _iirfX;
    float[] _iirfY;
    boolean _isEqualized;
    boolean _hasEnvelope;
    
    public FFTutils(int i) {
	WINDOW_SIZE = WS2 = i;
	WS2 >>= 1;
	BIT_LEN
	    = (int) (Math.log((double) WINDOW_SIZE) / 0.693147180559945 + 0.5);
	_normF = 2.0F / (float) WINDOW_SIZE;
	_hasEnvelope = false;
	_isEqualized = false;
	initFFTtables();
    }
    
    void initFFTtables() {
	_cosLUT = new float[BIT_LEN];
	_sinLUT = new float[BIT_LEN];
	_fftBuffer = new float[WINDOW_SIZE][2];
	_fft_result = new float[WS2];
	float f = 3.1415927F;
	for (int i = 0; i < BIT_LEN; i++) {
	    _cosLUT[i] = (float) Math.cos((double) f);
	    _sinLUT[i] = (float) Math.sin((double) f);
	    f *= 0.5;
	}
	_bitrevtable = new int[WINDOW_SIZE];
	for (int i = 0; i < WINDOW_SIZE; i++)
	    _bitrevtable[i] = i;
	int i = 0;
	int i_0_ = 0;
	for (/**/; i < WINDOW_SIZE; i++) {
	    if (i_0_ > i) {
		int i_1_ = _bitrevtable[i];
		_bitrevtable[i] = _bitrevtable[i_0_];
		_bitrevtable[i_0_] = i_1_;
	    }
	    int i_2_;
	    for (i_2_ = WS2; i_2_ >= 1 && i_0_ >= i_2_; i_2_ >>= 1)
		i_0_ -= i_2_;
	    i_0_ += i_2_;
	}
    }
    
    void useEqualizer(boolean bool) {
	_isEqualized = bool;
	if (bool) {
	    float f = -0.02F;
	    float f_3_ = 1.0F / (float) WS2;
	    _equalize = new float[WS2];
	    for (int i = 0; i < WS2; i++)
		_equalize[i]
		    = f * (float) Math.log((double) (WS2 - i) * (double) f_3_);
	}
    }
    
    void useEnvelope(boolean bool, float f) {
	_hasEnvelope = bool;
	if (bool) {
	    float f_4_ = 1.0F / (float) WINDOW_SIZE * TWO_PI;
	    _envelope = new float[WINDOW_SIZE];
	    if (f == 1.0F) {
		for (int i = 0; i < WINDOW_SIZE; i++)
		    _envelope[i]
			= (0.5F
			   + 0.5F * (float) Math.sin((double) ((float) i * f_4_
							       - HALF_PI)));
	    } else {
		for (int i = 0; i < WINDOW_SIZE; i++)
		    _envelope[i]
			= (float) (Math.pow
				   ((double) (0.5F
					      + (0.5F
						 * (float) (Math.sin
							    ((double) (((float) i
									* f_4_)
								       - HALF_PI))))),
				    (double) f));
	    }
	}
    }
    
    float[] computeFFT(float[] fs) {
	if (_hasEnvelope) {
	    for (int i = 0; i < WINDOW_SIZE; i++) {
		int i_5_ = _bitrevtable[i];
		if (i_5_ < WINDOW_SIZE)
		    _fftBuffer[i][0] = fs[i_5_] * _envelope[i_5_];
		else
		    _fftBuffer[i][0] = 0.0F;
		_fftBuffer[i][1] = 0.0F;
	    }
	} else {
	    for (int i = 0; i < WINDOW_SIZE; i++) {
		int i_6_ = _bitrevtable[i];
		if (i_6_ < WINDOW_SIZE)
		    _fftBuffer[i][0] = fs[i_6_];
		else
		    _fftBuffer[i][0] = 0.0F;
		_fftBuffer[i][1] = 0.0F;
	    }
	}
	int i = 1;
	int i_7_ = 2;
	int i_8_ = 0;
	for (/**/; i <= BIT_LEN; i++) {
	    int i_9_ = i_7_ >> 1;
	    float f = _cosLUT[i_8_];
	    float f_10_ = _sinLUT[i_8_++];
	    float f_11_ = 1.0F;
	    float f_12_ = 0.0F;
	    for (int i_13_ = 1; i_13_ <= i_9_; i_13_++) {
		for (int i_14_ = i_13_; i_14_ <= WINDOW_SIZE; i_14_ += i_7_) {
		    int i_15_ = i_14_ + i_9_;
		    int i_16_ = i_15_ - 1;
		    int i_17_ = i_14_ - 1;
		    float f_18_ = (_fftBuffer[i_16_][0] * f_11_
				   - f_12_ * _fftBuffer[i_16_][1]);
		    float f_19_ = (_fftBuffer[i_16_][1] * f_11_
				   + f_12_ * _fftBuffer[i_16_][0]);
		    _fftBuffer[i_16_][0] = _fftBuffer[i_17_][0] - f_18_;
		    _fftBuffer[i_16_][1] = _fftBuffer[i_17_][1] - f_19_;
		    _fftBuffer[i_17_][0] += f_18_;
		    _fftBuffer[i_17_][1] += f_19_;
		}
		float f_20_ = f_11_ * f - f_10_ * f_12_;
		f_12_ = f * f_12_ + f_10_ * f_11_;
		f_11_ = f_20_;
	    }
	    i_7_ <<= 1;
	}
	if (_isEqualized) {
	    for (int i_21_ = 0; i_21_ < WS2; i_21_++)
		_fft_result[i_21_]
		    = (_equalize[i_21_]
		       * (float) Math.sqrt((double) ((_fftBuffer[i_21_][0]
						      * _fftBuffer[i_21_][0])
						     + (_fftBuffer[i_21_][1]
							* (_fftBuffer[i_21_]
							   [1])))));
	} else {
	    for (int i_22_ = 0; i_22_ < WS2; i_22_++)
		_fft_result[i_22_]
		    = (_normF
		       * (float) Math.sqrt((double) ((_fftBuffer[i_22_][0]
						      * _fftBuffer[i_22_][0])
						     + (_fftBuffer[i_22_][1]
							* (_fftBuffer[i_22_]
							   [1])))));
	}
	return _fft_result;
    }
    
    void initFilter(float f, int i) {
	_iirfX = new float[i + 1];
	_iirfY = new float[i];
	_iirfX[0] = 1.0F - f;
	_iirfY[0] = f;
    }
    
    float[] applyFilter(float[] fs) {
	float[] fs_23_ = new float[fs.length];
	for (int i = _iirfY.length; i < fs.length; i++) {
	    float f = fs[i] * _iirfX[0];
	    for (int i_24_ = 0; i_24_ < _iirfY.length; i_24_++) {
		int i_25_ = i_24_ + 1;
		f += (fs[i - i_25_] * _iirfX[i_25_]
		      + fs_23_[i - i_25_] * _iirfY[i_24_]);
	    }
	    fs_23_[i] = f;
	}
	return fs_23_;
    }
}
