import { useEffect, useRef } from 'react';
import * as Blockly from 'blockly';
import { defineCustomBlocks, toolboxXml } from '../blocks/customBlocks';
import { workspaceToJsonString } from '../generator/jsonGenerator';
import './BlocklyWorkspace.css';

/**
 * React component that hosts a Blockly workspace.
 * - Initializes after mount
 * - Disposes on unmount
 * - Listens for changes and regenerates JSON
 * - Resizes on window resize
 */
export default function BlocklyWorkspace({ onJsonChange }) {
  const blocklyDivRef = useRef(null);
  const workspaceRef = useRef(null);
  const onJsonChangeRef = useRef(onJsonChange);

  // Keep callback ref up to date without re-attaching listeners
  useEffect(() => {
    onJsonChangeRef.current = onJsonChange;
  }, [onJsonChange]);

  useEffect(() => {
    if (!blocklyDivRef.current) return;

    // Register custom blocks once
    defineCustomBlocks();

    const workspace = Blockly.inject(blocklyDivRef.current, {
      toolbox: toolboxXml,
      scrollbars: true,
      trashcan: true,
      move: {
        scrollbars: true,
        drag: true,
        wheel: true,
      },
      grid: {
        spacing: 20,
        length: 3,
        colour: '#ccc',
        snap: true,
      },
      zoom: {
        controls: true,
        wheel: true,
        startScale: 1.0,
        maxScale: 2,
        minScale: 0.5,
        scaleSpeed: 1.1,
      },
      renderer: 'geras',
    });

    workspaceRef.current = workspace;

    // Emit initial (empty) JSON
    const emitJson = () => {
      if (onJsonChangeRef.current) {
        onJsonChangeRef.current(workspaceToJsonString(workspace));
      }
    };
    emitJson();

    // Listen for any change that affects the generated JSON
    const changeListener = (event) => {
      // Ignore UI-only events that do not alter the program structure
      if (
        event.type === Blockly.Events.UI ||
        event.type === Blockly.Events.VIEWPORT_CHANGE ||
        event.type === Blockly.Events.THEME_CHANGE ||
        event.type === Blockly.Events.SELECTED
      ) {
        return;
      }
      emitJson();
    };
    workspace.addChangeListener(changeListener);

    // Resize handler
    const handleResize = () => {
      Blockly.svgResize(workspace);
    };
    window.addEventListener('resize', handleResize);
    // Initial resize after a short delay so layout is ready
    const resizeTimer = setTimeout(handleResize, 50);

    return () => {
      clearTimeout(resizeTimer);
      window.removeEventListener('resize', handleResize);
      workspace.removeChangeListener(changeListener);
      workspace.dispose();
      workspaceRef.current = null;
    };
  }, []); // empty deps: run once on mount

  const handleClear = () => {
    if (workspaceRef.current) {
      workspaceRef.current.clear();
    }
  };

  return (
    <div className="blockly-container">
      <div className="blockly-toolbar">
        <button type="button" className="btn btn-clear" onClick={handleClear}>
          Clear workspace
        </button>
      </div>
      <div ref={blocklyDivRef} className="blockly-div" />
    </div>
  );
}
