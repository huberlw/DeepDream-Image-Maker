import './Buttons.css'
import React from 'react'

function DownloadBtn(props) {
    return (
        <button className="DreamifyButtons" onClick={props.onClick}>Download Image</button>
        )
}

export default DownloadBtn
