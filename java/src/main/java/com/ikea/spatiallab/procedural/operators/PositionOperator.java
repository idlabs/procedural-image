/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural.operators;

/**
 * @author rsahlin
 *
 */
public class PositionOperator extends OperatorImplementation {

    /**
     * @param operator
     */
    public PositionOperator(Operator operator) {
        super(operator);
    }

    @Override
    protected void addOpcode(OpcodeData opCodes) {
        opCodes.addOpcode(getOperator().opCode);
    }

    @Override
    protected OpcodeInput[] getInputs() {
        return null;
    }

}
