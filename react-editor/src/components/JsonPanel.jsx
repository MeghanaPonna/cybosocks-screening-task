import { useState } from 'react';
import './JsonPanel.css';

/**
 * Displays the live JSON output and a Copy button.
 */
export default function JsonPanel({ json }) {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(json);
      setCopied(true);
      setTimeout(() => setCopied(false), 1800);
    } catch {
      // Fallback for older browsers / insecure context
      try {
        const textarea = document.createElement('textarea');
        textarea.value = json;
        textarea.style.position = 'fixed';
        textarea.style.left = '-9999px';
        document.body.appendChild(textarea);
        textarea.select();
        document.execCommand('copy');
        document.body.removeChild(textarea);
        setCopied(true);
        setTimeout(() => setCopied(false), 1800);
      } catch {
        alert('Could not copy to clipboard. Please select and copy manually.');
      }
    }
  };

  return (
    <div className="json-panel">
      <div className="json-panel-header">
        <h2>JSON Output</h2>
        <button
          type="button"
          className={`btn btn-copy ${copied ? 'copied' : ''}`}
          onClick={handleCopy}
        >
          {copied ? 'Copied!' : 'Copy JSON'}
        </button>
      </div>
      <pre className="json-content">{json}</pre>
    </div>
  );
}
