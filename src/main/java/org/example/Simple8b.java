package org.example;

import java.util.Arrays;

public class Simple8b {

    private int[] AVAILABLE_BITS = {120, 60, 60, 60, 60, 60, 60, 56, 56, 54, 60, 55, 60, 52, 56, 60, 48, 51, 54, 57, 60,
            42, 44, 46, 48, 50, 52, 54, 56, 58, 60, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60,
            -1, -1, -1, -1};

    private int[] BITS_TO_COUNT = {120, 60, 30, 20, 15, 12, 10, 8, 7, 6, 6, 5, 5, 4, 4, 4, 3, 3, 3, 3, 3,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1};

    public int bits(long value) {
        return Long.SIZE - Long.numberOfLeadingZeros(value);
    }

    public int compress(long[] input, int inputPos, int amount, long[] output, int outputPos) {
        int startOutputPos = outputPos;
        for(int endPos = inputPos + amount; inputPos < endPos;) {
            int integersToCompress = 0;
            int maxBitsRequired, nextBitsRequired, toCompressBits;

            for(toCompressBits = nextBitsRequired = bits(input[inputPos]); (integersToCompress + 1) * (maxBitsRequired = Math.max(nextBitsRequired, toCompressBits)) <= AVAILABLE_BITS[maxBitsRequired];) {
                toCompressBits = maxBitsRequired;
                if(inputPos + integersToCompress + 1 < endPos) {
                    nextBitsRequired = bits(input[inputPos + ++integersToCompress]);
                } else {
                    ++integersToCompress;
                    break;
                }
            }

            while(integersToCompress < BITS_TO_COUNT[toCompressBits]) {
                toCompressBits++;
            }

            switch(toCompressBits) {
                case 0:
                    if(integersToCompress == 240) {
                        encode0(input, inputPos, output, outputPos);
                        inputPos += 240;
                    } else {
                        encode1(input, inputPos, output, outputPos);
                        inputPos += 120;
                    }
                    break;
                case 1:
                    encode2(input, inputPos, output, outputPos);
                    inputPos += 60;
                    break;
                case 2:
                    encode3(input, inputPos, output, outputPos);
                    inputPos += 30;
                    break;
                case 3:
                    encode4(input, inputPos, output, outputPos);
                    inputPos += 20;
                    break;
                case 4:
                    encode5(input, inputPos, output, outputPos);
                    inputPos += 15;
                    break;
                case 5:
                    encode6(input, inputPos, output, outputPos);
                    inputPos += 12;
                    break;
                case 6:
                    encode7(input, inputPos, output, outputPos);
                    inputPos += 10;
                    break;
                case 7:
                    encode8(input, inputPos, output, outputPos);
                    inputPos += 8;
                    break;
                case 8:
                    encode9(input, inputPos, output, outputPos);
                    inputPos += 7;
                    break;
                case 9, 10:
                    encode10(input, inputPos, output, outputPos);
                    inputPos += 6;
                    break;
                case 11, 12:
                    encode11(input, inputPos, output, outputPos);
                    inputPos += 5;
                    break;
                case 13, 14, 15:
                    encode12(input, inputPos, output, outputPos);
                    inputPos += 4;
                    break;
                case 16, 17, 18, 19, 20:
                    encode13(input, inputPos, output, outputPos);
                    inputPos += 3;
                    break;
                case 21, 22, 23, 24, 25, 26, 27, 28, 29, 30:
                    encode14(input, inputPos, output, outputPos);
                    inputPos += 2;
                    break;
                case 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                     41, 42, 43, 44, 45, 46, 47, 48, 49, 50,
                     51, 52, 53, 54, 55, 56, 57, 58, 59, 60:
                    encode15(input, inputPos, output, outputPos);
                    inputPos += 1;
                    break;
                default:
                    throw new RuntimeException("This compressor does not support values larger than 2^60");
            }
            outputPos++;
        }
        return outputPos - startOutputPos;
    }

    public void decompress(long[] input, int inputPos, int amount, long[] output, int outputPos) {
        for(int endPos = inputPos + amount; inputPos < endPos; inputPos++) {
            int selector = (int) (input[inputPos] >>> 60);

            switch(selector) {
                case 0:
                    decode0(input, inputPos, output, outputPos);
                    outputPos += 240;
                    break;
                case 1:
                    decode1(input, inputPos, output, outputPos);
                    outputPos += 120;
                    break;
                case 2:
                    decode2(input, inputPos, output, outputPos);
                    outputPos += 60;
                    break;
                case 3:
                    decode3(input, inputPos, output, outputPos);
                    outputPos += 30;
                    break;
                case 4:
                    decode4(input, inputPos, output, outputPos);
                    outputPos += 20;
                    break;
                case 5:
                    decode5(input, inputPos, output, outputPos);
                    outputPos += 15;
                    break;
                case 6:
                    decode6(input, inputPos, output, outputPos);
                    outputPos += 12;
                    break;
                case 7:
                    decode7(input, inputPos, output, outputPos);
                    outputPos += 10;
                    break;
                case 8:
                    decode8(input, inputPos, output, outputPos);
                    outputPos += 8;
                    break;
                case 9:
                    decode9(input, inputPos, output, outputPos);
                    outputPos += 7;
                    break;
                case 10:
                    decode10(input, inputPos, output, outputPos);
                    outputPos += 6;
                    break;
                case 11:
                    decode11(input, inputPos, output, outputPos);
                    outputPos += 5;
                    break;
                case 12:
                    decode12(input, inputPos, output, outputPos);
                    outputPos += 4;
                    break;
                case 13:
                    decode13(input, inputPos, output, outputPos);
                    outputPos += 3;
                    break;
                case 14:
                    decode14(input, inputPos, output, outputPos);
                    outputPos += 2;
                    break;
                case 15:
                    decode15(input, inputPos, output, outputPos);
                    outputPos += 1;
                    break;
            }
        }
    }

    private void encode0(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] = 0;
    }

    private void encode1(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] |= 1L << 60;
    }

    private void encode2(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] |= 2L << 60;
        output[outputPos] |= (input[startPos]) << 59;
        output[outputPos] |= (input[startPos + 1]) << 58;
        output[outputPos] |= (input[startPos + 2]) << 57;
        output[outputPos] |= (input[startPos + 3]) << 56;
        output[outputPos] |= (input[startPos + 4]) << 55;
        output[outputPos] |= (input[startPos + 5]) << 54;
        output[outputPos] |= (input[startPos + 6]) << 53;
        output[outputPos] |= (input[startPos + 7]) << 52;
        output[outputPos] |= (input[startPos + 8]) << 51;
        output[outputPos] |= (input[startPos + 9]) << 50;
        output[outputPos] |= (input[startPos + 10]) << 49;
        output[outputPos] |= (input[startPos + 11]) << 48;
        output[outputPos] |= (input[startPos + 12]) << 47;
        output[outputPos] |= (input[startPos + 13]) << 46;
        output[outputPos] |= (input[startPos + 14]) << 45;
        output[outputPos] |= (input[startPos + 15]) << 44;
        output[outputPos] |= (input[startPos + 16]) << 43;
        output[outputPos] |= (input[startPos + 17]) << 42;
        output[outputPos] |= (input[startPos + 18]) << 41;
        output[outputPos] |= (input[startPos + 19]) << 40;
        output[outputPos] |= (input[startPos + 20]) << 39;
        output[outputPos] |= (input[startPos + 21]) << 38;
        output[outputPos] |= (input[startPos + 22]) << 37;
        output[outputPos] |= (input[startPos + 23]) << 36;
        output[outputPos] |= (input[startPos + 24]) << 35;
        output[outputPos] |= (input[startPos + 25]) << 34;
        output[outputPos] |= (input[startPos + 26]) << 33;
        output[outputPos] |= (input[startPos + 27]) << 32;
        output[outputPos] |= (input[startPos + 28]) << 31;
        output[outputPos] |= (input[startPos + 29]) << 30;
        output[outputPos] |= (input[startPos + 30]) << 29;
        output[outputPos] |= (input[startPos + 31]) << 28;
        output[outputPos] |= (input[startPos + 32]) << 27;
        output[outputPos] |= (input[startPos + 33]) << 26;
        output[outputPos] |= (input[startPos + 34]) << 25;
        output[outputPos] |= (input[startPos + 35]) << 24;
        output[outputPos] |= (input[startPos + 36]) << 23;
        output[outputPos] |= (input[startPos + 37]) << 22;
        output[outputPos] |= (input[startPos + 38]) << 21;
        output[outputPos] |= (input[startPos + 39]) << 20;
        output[outputPos] |= (input[startPos + 40]) << 19;
        output[outputPos] |= (input[startPos + 41]) << 18;
        output[outputPos] |= (input[startPos + 42]) << 17;
        output[outputPos] |= (input[startPos + 43]) << 16;
        output[outputPos] |= (input[startPos + 44]) << 15;
        output[outputPos] |= (input[startPos + 45]) << 14;
        output[outputPos] |= (input[startPos + 46]) << 13;
        output[outputPos] |= (input[startPos + 47]) << 12;
        output[outputPos] |= (input[startPos + 48]) << 11;
        output[outputPos] |= (input[startPos + 49]) << 10;
        output[outputPos] |= (input[startPos + 50]) << 9;
        output[outputPos] |= (input[startPos + 51]) << 8;
        output[outputPos] |= (input[startPos + 52]) << 7;
        output[outputPos] |= (input[startPos + 53]) << 6;
        output[outputPos] |= (input[startPos + 54]) << 5;
        output[outputPos] |= (input[startPos + 55]) << 4;
        output[outputPos] |= (input[startPos + 56]) << 3;
        output[outputPos] |= (input[startPos + 57]) << 2;
        output[outputPos] |= (input[startPos + 58]) << 1;
        output[outputPos] |= (input[startPos + 59]);
    }

    private void encode3(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] |= 3L << 60;
        output[outputPos] |= (input[startPos]) << 58;
        output[outputPos] |= (input[startPos + 1]) << 56;
        output[outputPos] |= (input[startPos + 2]) << 54;
        output[outputPos] |= (input[startPos + 3]) << 52;
        output[outputPos] |= (input[startPos + 4]) << 50;
        output[outputPos] |= (input[startPos + 5]) << 48;
        output[outputPos] |= (input[startPos + 6]) << 46;
        output[outputPos] |= (input[startPos + 7]) << 44;
        output[outputPos] |= (input[startPos + 8]) << 42;
        output[outputPos] |= (input[startPos + 9]) << 40;
        output[outputPos] |= (input[startPos + 10]) << 38;
        output[outputPos] |= (input[startPos + 11]) << 36;
        output[outputPos] |= (input[startPos + 12]) << 34;
        output[outputPos] |= (input[startPos + 13]) << 32;
        output[outputPos] |= (input[startPos + 14]) << 30;
        output[outputPos] |= (input[startPos + 15]) << 28;
        output[outputPos] |= (input[startPos + 16]) << 26;
        output[outputPos] |= (input[startPos + 17]) << 24;
        output[outputPos] |= (input[startPos + 18]) << 22;
        output[outputPos] |= (input[startPos + 19]) << 20;
        output[outputPos] |= (input[startPos + 20]) << 18;
        output[outputPos] |= (input[startPos + 21]) << 16;
        output[outputPos] |= (input[startPos + 22]) << 14;
        output[outputPos] |= (input[startPos + 23]) << 12;
        output[outputPos] |= (input[startPos + 24]) << 10;
        output[outputPos] |= (input[startPos + 25]) << 8;
        output[outputPos] |= (input[startPos + 26]) << 6;
        output[outputPos] |= (input[startPos + 27]) << 4;
        output[outputPos] |= (input[startPos + 28]) << 2;
        output[outputPos] |= (input[startPos + 29]);
    }

    private void encode4(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] |= 4L << 60;
        output[outputPos] |= (input[startPos]) << 57;
        output[outputPos] |= (input[startPos + 1]) << 54;
        output[outputPos] |= (input[startPos + 2]) << 51;
        output[outputPos] |= (input[startPos + 3]) << 48;
        output[outputPos] |= (input[startPos + 4]) << 45;
        output[outputPos] |= (input[startPos + 5]) << 42;
        output[outputPos] |= (input[startPos + 6]) << 39;
        output[outputPos] |= (input[startPos + 7]) << 36;
        output[outputPos] |= (input[startPos + 8]) << 33;
        output[outputPos] |= (input[startPos + 9]) << 30;
        output[outputPos] |= (input[startPos + 10]) << 27;
        output[outputPos] |= (input[startPos + 11]) << 24;
        output[outputPos] |= (input[startPos + 12]) << 21;
        output[outputPos] |= (input[startPos + 13]) << 18;
        output[outputPos] |= (input[startPos + 14]) << 15;
        output[outputPos] |= (input[startPos + 15]) << 12;
        output[outputPos] |= (input[startPos + 16]) << 9;
        output[outputPos] |= (input[startPos + 17]) << 6;
        output[outputPos] |= (input[startPos + 18]) << 3;
        output[outputPos] |= (input[startPos + 19]);
    }

    private void encode5(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] |= 5L << 60;
        output[outputPos] |= (input[startPos]) << 56;
        output[outputPos] |= (input[startPos + 1]) << 52;
        output[outputPos] |= (input[startPos + 2]) << 48;
        output[outputPos] |= (input[startPos + 3]) << 44;
        output[outputPos] |= (input[startPos + 4]) << 40;
        output[outputPos] |= (input[startPos + 5]) << 36;
        output[outputPos] |= (input[startPos + 6]) << 32;
        output[outputPos] |= (input[startPos + 7]) << 28;
        output[outputPos] |= (input[startPos + 8]) << 24;
        output[outputPos] |= (input[startPos + 9]) << 20;
        output[outputPos] |= (input[startPos + 10]) << 16;
        output[outputPos] |= (input[startPos + 11]) << 12;
        output[outputPos] |= (input[startPos + 12]) << 8;
        output[outputPos] |= (input[startPos + 13]) << 4;
        output[outputPos] |= (input[startPos + 14]);
    }

    private void encode6(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] |= 6L << 60;
        output[outputPos] |= (input[startPos]) << 55;
        output[outputPos] |= (input[startPos + 1]) << 50;
        output[outputPos] |= (input[startPos + 2]) << 45;
        output[outputPos] |= (input[startPos + 3]) << 40;
        output[outputPos] |= (input[startPos + 4]) << 35;
        output[outputPos] |= (input[startPos + 5]) << 30;
        output[outputPos] |= (input[startPos + 6]) << 25;
        output[outputPos] |= (input[startPos + 7]) << 20;
        output[outputPos] |= (input[startPos + 8]) << 15;
        output[outputPos] |= (input[startPos + 9]) << 10;
        output[outputPos] |= (input[startPos + 10]) << 5;
        output[outputPos] |= (input[startPos + 11]);
    }

    private void encode7(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] |= 7L << 60;
        output[outputPos] |= (input[startPos]) << 54;
        output[outputPos] |= (input[startPos + 1]) << 48;
        output[outputPos] |= (input[startPos + 2]) << 42;
        output[outputPos] |= (input[startPos + 3]) << 36;
        output[outputPos] |= (input[startPos + 4]) << 30;
        output[outputPos] |= (input[startPos + 5]) << 24;
        output[outputPos] |= (input[startPos + 6]) << 18;
        output[outputPos] |= (input[startPos + 7]) << 12;
        output[outputPos] |= (input[startPos + 8]) << 6;
        output[outputPos] |= (input[startPos + 9]);
    }

    private void encode8(long[] input, int startPos, long[] output, int outputPos) {
        output[outputPos] |= 8L << 60;
        output[outputPos] |= (input[startPos]) << 49;
        output[outputPos] |= (input[startPos + 1]) << 42;
        output[outputPos] |= (input[startPos + 2]) << 35;
        output[outputPos] |= (input[startPos + 3]) << 28;
        output[outputPos] |= (input[startPos + 4]) << 21;
        output[outputPos] |= (input[startPos + 5]) << 14;
        output[outputPos] |= (input[startPos + 6]) << 7;
        output[outputPos] |= (input[startPos + 7]);
    }

    private void encode9(long[] input, int startPos, long[] output, int outputPos) {
        output[outputPos] |= 9L << 60;
        output[outputPos] |= (input[startPos]) << 48;
        output[outputPos] |= (input[startPos + 1]) << 40;
        output[outputPos] |= (input[startPos + 2]) << 32;
        output[outputPos] |= (input[startPos + 3]) << 24;
        output[outputPos] |= (input[startPos + 4]) << 16;
        output[outputPos] |= (input[startPos + 5]) << 8;
        output[outputPos] |= (input[startPos + 6]);
    }

    private void encode10(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] |= 10L << 60;
        output[outputPos] |= (input[startPos]) << 50;
        output[outputPos] |= (input[startPos + 1]) << 40;
        output[outputPos] |= (input[startPos + 2]) << 30;
        output[outputPos] |= (input[startPos + 3]) << 20;
        output[outputPos] |= (input[startPos + 4]) << 10;
        output[outputPos] |= (input[startPos + 5]);
    }

    private void encode11(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] |= 11L << 60;
        output[outputPos] |= (input[startPos]) << 48;
        output[outputPos] |= (input[startPos + 1]) << 36;
        output[outputPos] |= (input[startPos + 2]) << 24;
        output[outputPos] |= (input[startPos + 3]) << 12;
        output[outputPos] |= (input[startPos + 4]);
    }

    private static void encode12(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] |= 12L << 60;
        output[outputPos] |= (input[startPos]) << 45;
        output[outputPos] |= (input[startPos + 1]) << 30;
        output[outputPos] |= (input[startPos + 2]) << 15;
        output[outputPos] |= (input[startPos + 3]);
    }

    private void encode13(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] |= 13L << 60;
        output[outputPos] |= (input[startPos]) << 40;
        output[outputPos] |= (input[startPos + 1]) << 20;
        output[outputPos] |= (input[startPos + 2]);
    }

    private void encode14(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] |= 14L << 60;
        output[outputPos] |= (input[startPos]) << 30;
        output[outputPos] |= (input[startPos + 1]);
    }

    private void encode15(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] |= 15L << 60;
        output[outputPos] |= (input[startPos]);
    }

    private void decode0(final long[] input, int startPos, final long[] output, int outputPos) {
        //Arrays.fill(output, outputPos, output.length - 1, 0);
    }

    private void decode1(final long[] input, int startPos, final long[] output, int outputPos) {
        //Arrays.fill(output, outputPos, output.length - 1, 0);
    }

    private void decode2(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] = (input[startPos] >>> 59) & 1;
        output[outputPos+1] = (input[startPos] >>> 58) & 1;
        output[outputPos+2] = (input[startPos] >>> 57) & 1;
        output[outputPos+3] = (input[startPos] >>> 56) & 1;
        output[outputPos+4] = (input[startPos] >>> 55) & 1;
        output[outputPos+5] = (input[startPos] >>> 54) & 1;
        output[outputPos+6] = (input[startPos] >>> 53) & 1;
        output[outputPos+7] = (input[startPos] >>> 52) & 1;
        output[outputPos+8] = (input[startPos] >>> 51) & 1;
        output[outputPos+9] = (input[startPos] >>> 50) & 1;
        output[outputPos+10] = (input[startPos] >>> 49) & 1;
        output[outputPos+11] = (input[startPos] >>> 48) & 1;
        output[outputPos+12] = (input[startPos] >>> 47) & 1;
        output[outputPos+13] = (input[startPos] >>> 46) & 1;
        output[outputPos+14] = (input[startPos] >>> 45) & 1;
        output[outputPos+15] = (input[startPos] >>> 44) & 1;
        output[outputPos+16] = (input[startPos] >>> 43) & 1;
        output[outputPos+17] = (input[startPos] >>> 42) & 1;
        output[outputPos+18] = (input[startPos] >>> 41) & 1;
        output[outputPos+19] = (input[startPos] >>> 40) & 1;
        output[outputPos+20] = (input[startPos] >>> 39) & 1;
        output[outputPos+21] = (input[startPos] >>> 38) & 1;
        output[outputPos+22] = (input[startPos] >>> 37) & 1;
        output[outputPos+23] = (input[startPos] >>> 36) & 1;
        output[outputPos+24] = (input[startPos] >>> 35) & 1;
        output[outputPos+25] = (input[startPos] >>> 34) & 1;
        output[outputPos+26] = (input[startPos] >>> 33) & 1;
        output[outputPos+27] = (input[startPos] >>> 32) & 1;
        output[outputPos+28] = (input[startPos] >>> 31) & 1;
        output[outputPos+29] = (input[startPos] >>> 30) & 1;
        output[outputPos+30] = (input[startPos] >>> 29) & 1;
        output[outputPos+31] = (input[startPos] >>> 28) & 1;
        output[outputPos+32] = (input[startPos] >>> 27) & 1;
        output[outputPos+33] = (input[startPos] >>> 26) & 1;
        output[outputPos+34] = (input[startPos] >>> 25) & 1;
        output[outputPos+35] = (input[startPos] >>> 24) & 1;
        output[outputPos+36] = (input[startPos] >>> 23) & 1;
        output[outputPos+37] = (input[startPos] >>> 22) & 1;
        output[outputPos+38] = (input[startPos] >>> 21) & 1;
        output[outputPos+39] = (input[startPos] >>> 20) & 1;
        output[outputPos+40] = (input[startPos] >>> 19) & 1;
        output[outputPos+41] = (input[startPos] >>> 18) & 1;
        output[outputPos+42] = (input[startPos] >>> 17) & 1;
        output[outputPos+43] = (input[startPos] >>> 16) & 1;
        output[outputPos+44] = (input[startPos] >>> 15) & 1;
        output[outputPos+45] = (input[startPos] >>> 14) & 1;
        output[outputPos+46] = (input[startPos] >>> 13) & 1;
        output[outputPos+47] = (input[startPos] >>> 12) & 1;
        output[outputPos+48] = (input[startPos] >>> 11) & 1;
        output[outputPos+49] = (input[startPos] >>> 10) & 1;
        output[outputPos+50] = (input[startPos] >>> 9) & 1;
        output[outputPos+51] = (input[startPos] >>> 8) & 1;
        output[outputPos+52] = (input[startPos] >>> 7) & 1;
        output[outputPos+53] = (input[startPos] >>> 6) & 1;
        output[outputPos+54] = (input[startPos] >>> 5) & 1;
        output[outputPos+55] = (input[startPos] >>> 4) & 1;
        output[outputPos+56] = (input[startPos] >>> 3) & 1;
        output[outputPos+57] = (input[startPos] >>> 2) & 1;
        output[outputPos+58] = (input[startPos] >>> 1) & 1;
        output[outputPos+59] = input[startPos] & 1;
    }

    private void decode3(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] = (input[startPos] >>> 58) & 3;
        output[outputPos+1] = (input[startPos] >>> 56) & 3;
        output[outputPos+2] = (input[startPos] >>> 54) & 3;
        output[outputPos+3] = (input[startPos] >>> 52) & 3;
        output[outputPos+4] = (input[startPos] >>> 50) & 3;
        output[outputPos+5] = (input[startPos] >>> 48) & 3;
        output[outputPos+6] = (input[startPos] >>> 46) & 3;
        output[outputPos+7] = (input[startPos] >>> 44) & 3;
        output[outputPos+8] = (input[startPos] >>> 42) & 3;
        output[outputPos+9] = (input[startPos] >>> 40) & 3;
        output[outputPos+10] = (input[startPos] >>> 38) & 3;
        output[outputPos+11] = (input[startPos] >>> 36) & 3;
        output[outputPos+12] = (input[startPos] >>> 34) & 3;
        output[outputPos+13] = (input[startPos] >>> 32) & 3;
        output[outputPos+14] = (input[startPos] >>> 30) & 3;
        output[outputPos+15] = (input[startPos] >>> 28) & 3;
        output[outputPos+16] = (input[startPos] >>> 26) & 3;
        output[outputPos+17] = (input[startPos] >>> 24) & 3;
        output[outputPos+18] = (input[startPos] >>> 22) & 3;
        output[outputPos+19] = (input[startPos] >>> 20) & 3;
        output[outputPos+20] = (input[startPos] >>> 18) & 3;
        output[outputPos+21] = (input[startPos] >>> 16) & 3;
        output[outputPos+22] = (input[startPos] >>> 14) & 3;
        output[outputPos+23] = (input[startPos] >>> 12) & 3;
        output[outputPos+24] = (input[startPos] >>> 10) & 3;
        output[outputPos+25] = (input[startPos] >>> 8) & 3;
        output[outputPos+26] = (input[startPos] >>> 6) & 3;
        output[outputPos+27] = (input[startPos] >>> 4) & 3;
        output[outputPos+28] = (input[startPos] >>> 2) & 3;
        output[outputPos+29] = input[startPos] & 3;
    }

    private void decode4(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] = (input[startPos] >>> 57) & 7;
        output[outputPos+1] = (input[startPos] >>> 54) & 7;
        output[outputPos+2] = (input[startPos] >>> 51) & 7;
        output[outputPos+3] = (input[startPos] >>> 48) & 7;
        output[outputPos+4] = (input[startPos] >>> 45) & 7;
        output[outputPos+5] = (input[startPos] >>> 42) & 7;
        output[outputPos+6] = (input[startPos] >>> 39) & 7;
        output[outputPos+7] = (input[startPos] >>> 36) & 7;
        output[outputPos+8] = (input[startPos] >>> 33) & 7;
        output[outputPos+9] = (input[startPos] >>> 30) & 7;
        output[outputPos+10] = (input[startPos] >>> 27) & 7;
        output[outputPos+11] = (input[startPos] >>> 24) & 7;
        output[outputPos+12] = (input[startPos] >>> 21) & 7;
        output[outputPos+13] = (input[startPos] >>> 18) & 7;
        output[outputPos+14] = (input[startPos] >>> 15) & 7;
        output[outputPos+15] = (input[startPos] >>> 12) & 7;
        output[outputPos+16] = (input[startPos] >>> 9) & 7;
        output[outputPos+17] = (input[startPos] >>> 6) & 7;
        output[outputPos+18] = (input[startPos] >>> 3) & 7;
        output[outputPos+19] = input[startPos] & 7;
    }

    private void decode5(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] = (input[startPos] >>> 56) & 15;
        output[outputPos+1] = (input[startPos] >>> 52) & 15;
        output[outputPos+2] = (input[startPos] >>> 48) & 15;
        output[outputPos+3] = (input[startPos] >>> 44) & 15;
        output[outputPos+4] = (input[startPos] >>> 40) & 15;
        output[outputPos+5] = (input[startPos] >>> 36) & 15;
        output[outputPos+6] = (input[startPos] >>> 32) & 15;
        output[outputPos+7] = (input[startPos] >>> 28) & 15;
        output[outputPos+8] = (input[startPos] >>> 24) & 15;
        output[outputPos+9] = (input[startPos] >>> 20) & 15;
        output[outputPos+10] = (input[startPos] >>> 16) & 15;
        output[outputPos+11] = (input[startPos] >>> 12) & 15;
        output[outputPos+12] = (input[startPos] >>> 8) & 15;
        output[outputPos+13] = (input[startPos] >>> 4) & 15;
        output[outputPos+14] = input[startPos] & 15;
    }

    private void decode6(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] = (input[startPos] >>> 55) & 31;
        output[outputPos+1] = (input[startPos] >>> 50) & 31;
        output[outputPos+2] = (input[startPos] >>> 45) & 31;
        output[outputPos+3] = (input[startPos] >>> 40) & 31;
        output[outputPos+4] = (input[startPos] >>> 35) & 31;
        output[outputPos+5] = (input[startPos] >>> 30) & 31;
        output[outputPos+6] = (input[startPos] >>> 25) & 31;
        output[outputPos+7] = (input[startPos] >>> 20) & 31;
        output[outputPos+8] = (input[startPos] >>> 15) & 31;
        output[outputPos+9] = (input[startPos] >>> 10) & 31;
        output[outputPos+10] = (input[startPos] >>> 5) & 31;
        output[outputPos+11] = input[startPos] & 31;
    }

    private void decode7(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] = (input[startPos] >>> 54) & 63;
        output[outputPos+1] = (input[startPos] >>> 48) & 63;
        output[outputPos+2] = (input[startPos] >>> 42) & 63;
        output[outputPos+3] = (input[startPos] >>> 36) & 63;
        output[outputPos+4] = (input[startPos] >>> 30) & 63;
        output[outputPos+5] = (input[startPos] >>> 24) & 63;
        output[outputPos+6] = (input[startPos] >>> 18) & 63;
        output[outputPos+7] = (input[startPos] >>> 12) & 63;
        output[outputPos+8] = (input[startPos] >>> 6) & 63;
        output[outputPos+9] = input[startPos] & 63;
    }

    private void decode8(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] = (input[startPos] >>> 49) & 127;
        output[outputPos+1] = (input[startPos] >>> 42) & 127;
        output[outputPos+2] = (input[startPos] >>> 35) & 127;
        output[outputPos+3] = (input[startPos] >>> 28) & 127;
        output[outputPos+4] = (input[startPos] >>> 21) & 127;
        output[outputPos+5] = (input[startPos] >>> 14) & 127;
        output[outputPos+6] = (input[startPos] >>> 7) & 127;
        output[outputPos+7] = input[startPos] & 127;
    }

    private void decode9(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] = (input[startPos] >>> 48) & 255;
        output[outputPos+1] = (input[startPos] >>> 40) & 255;
        output[outputPos+2] = (input[startPos] >>> 32) & 255;
        output[outputPos+3] = (input[startPos] >>> 24) & 255;
        output[outputPos+4] = (input[startPos] >>> 16) & 255;
        output[outputPos+5] = (input[startPos] >>> 8) & 255;
        output[outputPos+6] = input[startPos] & 255;
    }

    private void decode10(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] = (input[startPos] >>> 50) & 1023;
        output[outputPos+1] = (input[startPos] >>> 40) & 1023;
        output[outputPos+2] = (input[startPos] >>> 30) & 1023;
        output[outputPos+3] = (input[startPos] >>> 20) & 1023;
        output[outputPos+4] = (input[startPos] >>> 10) & 1023;
        output[outputPos+5] = input[startPos] & 1023;
    }

    private void decode11(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] = (input[startPos] >>> 48) & 4095;
        output[outputPos+1] = (input[startPos] >>> 36) & 4095;
        output[outputPos+2] = (input[startPos] >>> 24) & 4095;
        output[outputPos+3] = (input[startPos] >>> 12) & 4095;
        output[outputPos+4] = input[startPos] & 4095;
    }

    private void decode12(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] = (input[startPos] >>> 45) & 32767;
        output[outputPos+1] = (input[startPos] >>> 30) & 32767;
        output[outputPos+2] = (input[startPos] >>> 15) & 32767;
        output[outputPos+3] = input[startPos] & 32767;
    }

    private void decode13(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] = (input[startPos] >>> 40) & 1048575;
        output[outputPos+1] = (input[startPos] >>> 20) & 1048575;
        output[outputPos+2] = input[startPos] & 1048575;
    }

    private void decode14(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] = (input[startPos] >>> 30) & 1073741823;
        output[outputPos+1] = input[startPos] & 1073741823;
    }

    private void decode15(final long[] input, int startPos, final long[] output, int outputPos) {
        output[outputPos] = input[startPos] & 1152921504606846975L;
    }
}
