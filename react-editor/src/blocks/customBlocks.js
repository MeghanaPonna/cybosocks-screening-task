/**
 * Custom Blockly block definitions for Cybosocks.
 * Four custom blocks: move, turn, say, repeat.
 * Does NOT reuse Blockly built-in loop/logic blocks.
 */
import * as Blockly from 'blockly';

/**
 * Register all custom blocks on Blockly.
 * Call once before creating the workspace.
 */
export function defineCustomBlocks() {
  // ----- MOVE -----
  Blockly.Blocks['cybosocks_move'] = {
    init: function () {
      this.appendDummyInput()
        .appendField('move')
        .appendField(
          new Blockly.FieldNumber(1, 0, 1000, 1),
          'STEPS'
        )
        .appendField('steps');
      this.setPreviousStatement(true, null);
      this.setNextStatement(true, null);
      this.setColour(160);
      this.setTooltip('Move the turtle forward by the given number of steps.');
      this.setHelpUrl('');
    },
  };

  // ----- TURN -----
  Blockly.Blocks['cybosocks_turn'] = {
    init: function () {
      this.appendDummyInput()
        .appendField('turn')
        .appendField(
          new Blockly.FieldDropdown([
            ['left', 'left'],
            ['right', 'right'],
          ]),
          'DIRECTION'
        );
      this.setPreviousStatement(true, null);
      this.setNextStatement(true, null);
      this.setColour(230);
      this.setTooltip('Turn the turtle 90 degrees left or right.');
      this.setHelpUrl('');
    },
  };

  // ----- SAY -----
  Blockly.Blocks['cybosocks_say'] = {
    init: function () {
      this.appendDummyInput()
        .appendField('say')
        .appendField(new Blockly.FieldTextInput('Hello!'), 'TEXT');
      this.setPreviousStatement(true, null);
      this.setNextStatement(true, null);
      this.setColour(60);
      this.setTooltip('Print a message to the console.');
      this.setHelpUrl('');
    },
  };

  // ----- REPEAT -----
  // Fully custom; does not use Blockly's controls_repeat.
  Blockly.Blocks['cybosocks_repeat'] = {
    init: function () {
      this.appendDummyInput()
        .appendField('repeat')
        .appendField(new Blockly.FieldNumber(3, 0, 1000, 1), 'TIMES')
        .appendField('times');
      this.appendStatementInput('BODY').setCheck(null).appendField('do');
      this.setPreviousStatement(true, null);
      this.setNextStatement(true, null);
      this.setColour(120);
      this.setTooltip(
        'Repeat the blocks inside exactly the given number of times. Nesting is supported.'
      );
      this.setHelpUrl('');
    },
  };
}

/**
 * Toolbox configuration for the four custom blocks.
 */
export const toolboxXml = {
  kind: 'categoryToolbox',
  contents: [
    {
      kind: 'category',
      name: 'Actions',
      colour: '160',
      contents: [
        { kind: 'block', type: 'cybosocks_move' },
        { kind: 'block', type: 'cybosocks_turn' },
        { kind: 'block', type: 'cybosocks_say' },
      ],
    },
    {
      kind: 'category',
      name: 'Control',
      colour: '120',
      contents: [{ kind: 'block', type: 'cybosocks_repeat' }],
    },
  ],
};
