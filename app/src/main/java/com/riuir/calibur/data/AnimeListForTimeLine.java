package com.riuir.calibur.data;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.riuir.calibur.ui.home.DramaTimelineFragment;

import java.util.List;

public class AnimeListForTimeLine {
    private int code;
    private String message;
    private Data data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }



    @Override
    public String toString() {
        return "AnimeListForTimeLine{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public class Data {
        private int min;
        private List<AnimeClassifyForDate> list;

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public List<AnimeClassifyForDate> getList() {
            return list;
        }

        public void setList(List<AnimeClassifyForDate> list) {
            this.list = list;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "min=" + min +
                    ", list=" + list +
                    '}';
        }
    }

    public class AnimeClassifyForDate extends AbstractExpandableItem<AnimeInfo> implements MultiItemEntity {
        private String date;
        private List<AnimeInfo> list;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<AnimeInfo> getList() {
            return list;
        }

        public void setList(List<AnimeInfo> list) {
            this.list = list;
        }

        @Override
        public String toString() {
            return "AnimeClassifyForDate{" +
                    "date='" + date + '\'' +
                    ", list=" + list +
                    '}';
        }

        //implements MultiItemEntity
        @Override
        public int getItemType() {
            return DramaTimelineFragment.TYPE_GROUP;
        }

        //extends AbstractExpandableItem<AnimeInfo>
        @Override
        public int getLevel() {
            return 0;
        }
    }

    public class AnimeInfo implements MultiItemEntity{
       private int id;
       private String name;
       private List<Tags> tags;
       private String timeLine;
       private String avatar;
       private String summary;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Tags> getTags() {
            return tags;
        }

        public void setTags(List<Tags> tags) {
            this.tags = tags;
        }

        public String getTimeLine() {
            return timeLine;
        }

        public void setTimeLine(String timeLine) {
            this.timeLine = timeLine;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        @Override
        public String toString() {
            return "AnimeInfo{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", tags=" + tags +
                    ", timeLine='" + timeLine + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", summary='" + summary + '\'' +
                    '}';
        }

        //implements MultiItemEntity
        @Override
        public int getItemType() {
            return DramaTimelineFragment.TYPE_CHILD;
        }
    }

    public class Tags{
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Tags{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
