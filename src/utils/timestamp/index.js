
function getPassTime(data) {

    var currentDate = new Date();
    var postDate = new Date(data);

    var timeDiff = currentDate.getTime() - postDate.getTime();

    var minDiff = Math.floor(timeDiff / (1000 * 60));
    var hoursDiff = Math.floor(timeDiff / (1000 * 60 * 60));
    var daysDiff = Math.floor(timeDiff / (1000 * 60 * 60 * 24));
    var monthsDiff = Math.floor(daysDiff / 30);

    if (minDiff < 60) {
        if (Math.floor(minDiff / 60) === 0) {
            minDiff = 1;
        }
        return (minDiff + "分钟前");
    } else if (hoursDiff < 24) {
        return (hoursDiff + "小时前");
    } else if (daysDiff < 30) {
        return (daysDiff + "天前");
    } else if (monthsDiff < 12) {
        return (monthsDiff + "个月前");
    } else data;
}

export {
    getPassTime
}