/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 * Blends foreground and background using a given blend mode and opacity value.
 * opacity float
 * foreground vec4
 * background vec4
 * blend_mode int
 * 
 * x := foreground, y := background, r := blending result, f := opacity.
 * 0 Normal r = x
 * 1 Linear Dodge r = x + y
 * 2 Subtract r = y - x
 * 3 Multiply r = x * y
 * 4 Divide r = y / x)
 * 5 Screen r = 1 - (1 - x) * (1 - y)
 * 6 Max r = { max(x.x, y.x), max(x.y, y.y), max(x.z, y.z)}
 * 7 Min r = { min(x.x, y.x), min(x.y, y.y), min(x.z, y.z)}
 * 
 */
public class BlendOperator extends OperatorImplementation {

    public static final String BLENDMODE = "blend_mode";

    private enum BlendInput implements OpcodeInput {
        foreground((byte) 0),
        background((byte) 1),
        opacity((byte) 2);

        private final byte index;

        BlendInput(byte index) {
            this.index = index;
        }

        @Override
        public String getName() {
            return name();
        }

        @Override
        public byte getIndex() {
            return index;
        }
    }

    public enum BlendMode {
        normal((byte) 0),
        linearDodge((byte) 1),
        subtract((byte) 2),
        multiply((byte) 3),
        divide((byte) 4),
        screen((byte) 5),
        max((byte) 6),
        min((byte) 7);

        public final byte value;

        BlendMode(byte value) {
            this.value = value;
        }

        public static BlendMode get(int value) {
            for (BlendMode b : values()) {
                if (b.value == value) {
                    return b;
                }
            }
            return null;
        }

    }

    protected BlendOperator(Operator operator) {
        super(operator);
    }

    /**
     * 
     * @return
     */
    public BlendMode getBlendMode() {
        return BlendMode.get(getFloatParameter(BLENDMODE).getAsIntValue());
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getOperator().opCode, getBlendMode().value);
    }

    @Override
    protected OpcodeInput[] getInputs() {
        return BlendInput.values();
    }

}
