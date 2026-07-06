/** Input con label asociada; el id enlaza ambos para accesibilidad. */
export function FormField({ id, label, type = 'text', value, onChange, required = true, ...rest }) {
  return (
    <div className="field">
      <label htmlFor={id}>{label}</label>
      <input
        id={id}
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        required={required}
        {...rest}
      />
    </div>
  );
}
