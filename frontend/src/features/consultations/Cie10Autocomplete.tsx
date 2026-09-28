import { apiClient } from '../../shared/http/apiClient';
import { useEffect, useState } from 'react';

interface Icd10Code {
  code: string;
  description: string;
}

interface Cie10AutocompleteProps {
  value: string;
  onChange: (code: string, description: string) => void;
  error?: string;
}

export function Cie10Autocomplete({ value, onChange, error }: Cie10AutocompleteProps) {
  const [query, setQuery] = useState(value);
  const [results, setResults] = useState<Icd10Code[]>([]);
  const [isOpen, setIsOpen] = useState(false);

  useEffect(() => {
    setQuery(value);
  }, [value]);

  useEffect(() => {
    const timer = setTimeout(async () => {
      if (query.trim().length >= 2) {
        try {
          const res = await apiClient.get<Icd10Code[]>(`/icd10?query=${encodeURIComponent(query)}`);
          setResults(res.data);
          setIsOpen(true);
        } catch (e) {
          console.error(e);
        }
      } else if (query.trim().length === 0) {
        setResults([]);
        setIsOpen(false);
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [query]);

  return (
    <div className="cie10-autocomplete" style={{ position: 'relative' }}>
      <input
        type="text"
        className="form-control"
        placeholder="Buscar CIE-10 (código o nombre)..."
        autoComplete="off"
        value={query}
        onChange={(e) => {
          setQuery(e.target.value);
          // Only clear if empty, else let them select
          if (!e.target.value) {
            onChange('', '');
          }
        }}
        onFocus={() => { if (results.length > 0) setIsOpen(true); }}
        onBlur={() => setTimeout(() => setIsOpen(false), 200)}
      />
      {error && <span className="field-error">{error}</span>}
      {isOpen && results.length > 0 && (
        <ul className="list-group" style={{ position: 'absolute', zIndex: 1000, width: '100%', maxHeight: '200px', overflowY: 'auto' }}>
          {results.map((r) => (
            <li
              key={r.code}
              className="list-group-item list-group-item-action"
              style={{ cursor: 'pointer' }}
              onClick={() => {
                setQuery(`${r.code} - ${r.description}`);
                onChange(r.code, r.description);
                setIsOpen(false);
              }}
            >
              <strong>{r.code}</strong> {r.description}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
