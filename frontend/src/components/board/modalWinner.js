import React from "react";
import "../../static/css/board/modalWinner.css";

export default class WinnerModal extends React.Component {
  render() {
    const { isVisible, onClose, message, message2 } = this.props;

    if (!isVisible) return null;
    const computedMessage2 = typeof message2 === "function" ? message2() : message2;

    return (
      <div className="modal-overlay">
        <div className="modal-content">
          <h2 className="modal-title">El ganador de la partida es:</h2>
          <p className="modal-message">
            <strong>{message}</strong>
          </p>
          <p className="modal-message-secondary">
            <strong>{computedMessage2}</strong>
          </p>
          <button className="modal-button" onClick={onClose}>
            Volver al Inicio
          </button>
        </div>
      </div>
    );
  }
}