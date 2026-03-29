import React from "react";
import "../../static/css/board/modal.css"; // Archivo CSS que contendrá los estilos del modal.

export default class modal extends React.Component {
  render() {
    const { isOpen, onClose, onConfirm } = this.props;

    if (!isOpen) return null; // Si el modal no está abierto, no se muestra nada.

    return (
      <div className="modal-overlay">
        <div className="modal-box">
          <p>
            ¿Deseas aumentar o reducir el número de territorios a colocar?
          </p>
          <div className="modal-buttons">
            <button className="btn-increase" onClick={() => onConfirm(1)}>
              Aumentar
            </button>
            <button className="btn-decrease" onClick={() => onConfirm(-1)}>
              Reducir
            </button>
          </div>
        </div>
      </div>
    );
  }
}
