# auto-video-ad-remove
## Functions
- Read in the input raw video(rgb)/audio(wav).
- Break the input video into a list of logical segments – shots.
- Give each shot a variety of quantitative weights such as – length of shot,audio levels, color statistics histogram.
- Using the above characteristics, decide whether a shot or a group of adjacent shots might be an advertisement
- Remove the shots that correspond to the advertisement. Write out the new video/audio file.
- If brands are detected, replace the old advertisement with a new advertisement to
- write out the new video/audio file.

## Implementation
- Use SIFT as a icon detection
- Use combine audio frame level characteristic and video frame histogram to detect ads

## Dependency
see `pom.xml`

## Video player
![](https://pic-1255943388.cos.ap-guangzhou.myqcloud.com/20190502015231.png)

## Run
```
java ImageDisplay [rgb path] [wav path]
```
