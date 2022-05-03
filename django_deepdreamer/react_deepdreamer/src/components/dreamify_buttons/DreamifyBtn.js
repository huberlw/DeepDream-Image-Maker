import './DreamifyBtn.css'
import React from 'react'

function DreamifyBtn(props) {
    return (
        <button className="dream-btn" onClick={props.onClick}>Dreamify</button>
    )
}

export default DreamifyBtn
