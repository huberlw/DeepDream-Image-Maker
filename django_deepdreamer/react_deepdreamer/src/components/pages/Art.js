import React, { useState }  from 'react';
import './Art.css';
import CloseIcon from '@mui/icons-material/Close';

export default function Art() {
    let data = [
        {
            id: 1,
            img: "../../../../../images/standard/einstein.png",
        },
        {
            id: 2,
            img: "../../../../../images/standard/google.png",
        },
        {
            id: 3,
            img: "../../../../../images/standard/penguin.png",
        },
        {
            id: 4,
            img: "../../../../../images/standard/dog.png",
        },
        {
            id: 5,
            img: "../../../../../images/standard/frog.png",
        },
        {
            id: 4,
            img: "../../../../../images/standard/lovelace.png",
        },
        {
            id: 6,
            img: "../../../../../images/standard/cow.png",
        },
        {
            id: 7,
            img: "../../../../../images/standard/washington.png",
        },
        {
            id: 8,
            img: "../../../../../images/standard/neumann.png",
        },
        {
            id: 9,
            img: "../../../../../images/standard/newton.png",
        },
    ]
    
    const [model, setModel] = useState(false);
    const [tmpImg, setTmpImg] = useState('');

    const fullScreen = (img) => {
        setTmpImg(img);
        setModel(true);
    }

    return (
        <>
        <div className={model ? "model open" : "model"}>
            <img src={tmpImg} />
            <CloseIcon onClick ={() => setModel(false)}/>
        </div>
        <div className="art">
            {
                data.map((item, index) => {
                    return(
                        <div className="art-images" key={index} onClick={() => fullScreen(item.img)}>
                            <img src={item.img} style={{width: '100%'}}/>
                        </div>
                    );
                })
            }
        </div>
        </>
    );
}