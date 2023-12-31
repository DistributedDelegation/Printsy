import React, { useState, useEffect } from 'react';
// styling by Mateusz Rybczonek:
// https://css-tricks.com/how-to-create-an-animated-countdown-timer-with-html-css-and-javascript/
import '../components/Timer.css';

const FULL_DASH_ARRAY = 283;
const WARNING_THRESHOLD = 10;
const ALERT_THRESHOLD = 5;

const COLOR_CODES = {
  info: {
    color: "green"
  },
  warning: {
    color: "orange",
    threshold: WARNING_THRESHOLD
  },
  alert: {
    color: "red",
    threshold: ALERT_THRESHOLD
  }
};

const Timer = ({ initialTime, onTimerEnd }) => {
  const [timeLeft, setTimeLeft] = useState(initialTime);
  const [timerInterval, setTimerInterval] = useState(null);
  const [remainingPathColor, setRemainingPathColor] = useState(COLOR_CODES.info.color);

  useEffect(() => {
    setTimeLeft(initialTime);
  }, [initialTime]);

  useEffect(() => {
    setRemainingPathColor(timeLeft <= COLOR_CODES.alert.threshold ? COLOR_CODES.alert.color 
                      : timeLeft <= COLOR_CODES.warning.threshold ? COLOR_CODES.warning.color 
                      : COLOR_CODES.info.color);

    if (timeLeft === 0) {
      clearInterval(timerInterval);
      if (onTimerEnd) {
        onTimerEnd();
      }
    }
  }, [timeLeft, timerInterval, onTimerEnd]);

  useEffect(() => {
    setTimerInterval(setInterval(() => {
      setTimeLeft((prevTime) => prevTime - 1);
    }, 1000));

    return () => {
      clearInterval(timerInterval);
    };
  }, []);

  const formatTime = (time) => {
    const minutes = Math.floor(time / 60);
    let seconds = time % 60;

    if (seconds < 10) {
      seconds = `0${seconds}`;
    }

    return `${minutes}:${seconds}`;
  };

  const calculateTimeFraction = () => {
    const rawTimeFraction = timeLeft / 120;
    return rawTimeFraction - (1 / 120) * (1 - rawTimeFraction);
  };

  const circleDasharray = `${(calculateTimeFraction() * FULL_DASH_ARRAY).toFixed(0)} 283`;

  return (
    <div className="base-timer">
      <svg className="base-timer__svg" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
        <g className="base-timer__circle">
          <circle className="base-timer__path-elapsed" cx="50" cy="50" r="45"></circle>
          <path
            id="base-timer-path-remaining"
            strokeDasharray="283"
            className={`base-timer__path-remaining ${remainingPathColor}`}
            d="M 50, 50 m -45, 0 a 45,45 0 1,0 90,0 a 45,45 0 1,0 -90,0"
            style={{ strokeDasharray: circleDasharray }}
          ></path>
        </g>
      </svg>
      <span id="base-timer-label" className="base-timer__label">
        {formatTime(timeLeft)}
      </span>
    </div>
  );
};

export default Timer;