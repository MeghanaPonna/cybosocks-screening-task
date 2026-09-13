/**
 * Custom recursive JSON generator for Cybosocks blocks.
 * Does NOT use Blockly's built-in JavaScript or Python generators.
 * Produces the exact schema required by the Java runtime.
 */

/**
 * Convert a single Blockly block into the required JSON object.
 * Returns null for unsupported / empty blocks.
 *
 * @param {import('blockly').Block} block
 * @returns {object|null}
 */
function blockToJson(block) {
  if (!block || block.isInsertionMarker()) {
    return null;
  }

  switch (block.type) {
    case 'cybosocks_move': {
      let steps = block.getFieldValue('STEPS');
      steps = Number(steps);
      if (!Number.isFinite(steps) || steps < 0) {
        steps = 0;
      }
      steps = Math.floor(steps);
      return { type: 'move', steps };
    }

    case 'cybosocks_turn': {
      const direction = block.getFieldValue('DIRECTION') || 'right';
      return { type: 'turn', direction };
    }

    case 'cybosocks_say': {
      const text = block.getFieldValue('TEXT') ?? '';
      return { type: 'say', text: String(text) };
    }

    case 'cybosocks_repeat': {
      let times = block.getFieldValue('TIMES');
      times = Number(times);
      if (!Number.isFinite(times) || times < 0) {
        times = 0;
      }
      times = Math.floor(times);

      const body = [];
      const bodyInput = block.getInput('BODY');
      if (bodyInput && bodyInput.connection) {
        let child = bodyInput.connection.targetBlock();
        while (child) {
          const json = blockToJson(child);
          if (json) {
            body.push(json);
          }
          child = child.getNextBlock();
        }
      }

      return { type: 'repeat', times, body };
    }

    default:
      return null;
  }
}

/**
 * Walk the top-level stack of blocks in the workspace and produce
 * the program JSON: { "blocks": [ ... ] }
 *
 * @param {import('blockly').Workspace} workspace
 * @returns {{ blocks: object[] }}
 */
export function workspaceToJson(workspace) {
  const blocks = [];
  const topBlocks = workspace.getTopBlocks(true);

  for (const top of topBlocks) {
    // Skip blocks that are only shadow / insertion markers
    if (top.isInsertionMarker()) continue;

    let current = top;
    while (current) {
      const json = blockToJson(current);
      if (json) {
        blocks.push(json);
      }
      current = current.getNextBlock();
    }
  }

  return { blocks };
}

/**
 * Produce a pretty-printed JSON string (2-space indent).
 *
 * @param {import('blockly').Workspace} workspace
 * @returns {string}
 */
export function workspaceToJsonString(workspace) {
  const program = workspaceToJson(workspace);
  return JSON.stringify(program, null, 2);
}
