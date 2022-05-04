import './Buttons.css'
import React from 'react'

function ResetBtn(props) {
    return (
        <button className="DreamifyButtons" onClick={props.onClick}>Reset</button>
    )
}

export default ResetBtn