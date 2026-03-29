import "../../static/css/board/modal.css";
import React from "react";
import Tablero from "../board/index";

export default class ModalPoderInterrogacion extends React.Component {
    render() {
        const { isVisible, onClose, message} = this.props;

        if (!isVisible) return null;

        return (
            <div className="modal-overlay">
                <div className="modal-box">
                <div className="modal-content">
                    <p>Tu puntuación en el poder es de:</p>
                    <h2>{message}</h2>
                    <p>Se guardará para el recuento final</p>
                        <button className="btn-decrease" onClick={onClose}>
                            Cerrar
                        </button>
                </div>
                </div>
            </div>
        );
    }
}
