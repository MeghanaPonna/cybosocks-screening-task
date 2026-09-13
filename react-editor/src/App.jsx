import { useState, useCallback } from 'react';
import BlocklyWorkspace from './components/BlocklyWorkspace';
import JsonPanel from './components/JsonPanel';
import './App.css';

const EMPTY_JSON = JSON.stringify({ blocks: [] }, null, 2);

function App() {
  const [json, setJson] = useState(EMPTY_JSON);

  const handleJsonChange = useCallback((newJson) => {
    setJson(newJson);
  }, []);

  return (
    <div className="app">
      <header className="app-header">
        <h1>🐢 Cybosocks Block Editor</h1>
        <p className="subtitle">
          Drag blocks → build a program → see live JSON
        </p>
      </header>

      <main className="app-main">
        <section className="workspace-section">
          <BlocklyWorkspace onJsonChange={handleJsonChange} />
        </section>

        <section className="json-section">
          <JsonPanel json={json} />
        </section>
      </main>
    </div>
  );
}

export default App;
