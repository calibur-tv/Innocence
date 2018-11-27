package calibur.core.http.models.user;

import java.util.List;

public class UserReplyCardInfo {
    private boolean noMore;
    private int total;
    private List<UserReplayCardInfoList> list;

    public boolean isNoMore() {
        return noMore;
    }

    public void setNoMore(boolean noMore) {
        this.noMore = noMore;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<UserReplayCardInfoList> getList() {
        return list;
    }

    public void setList(List<UserReplayCardInfoList> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "UserReplayCardInfoData{" +
                "noMore=" + noMore +
                ", total=" + total +
                ", list=" + list +
                '}';
    }

    public class UserReplayCardInfoList{
        private int id;
        private String content;
        private List<UserReplayCardInfoImage> images;
        private String created_at;
        private int floor_count;
        private UserReplayCardInfoBangumi bangumi;
        private UserReplayCardInfoPost post;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<UserReplayCardInfoImage> getImages() {
            return images;
        }

        public void setImages(List<UserReplayCardInfoImage> images) {
            this.images = images;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public int getFloor_count() {
            return floor_count;
        }

        public void setFloor_count(int floor_count) {
            this.floor_count = floor_count;
        }

        public UserReplayCardInfoBangumi getBangumi() {
            return bangumi;
        }

        public void setBangumi(UserReplayCardInfoBangumi bangumi) {
            this.bangumi = bangumi;
        }

        public UserReplayCardInfoPost getPost() {
            return post;
        }

        public void setPost(UserReplayCardInfoPost post) {
            this.post = post;
        }

        @Override
        public String toString() {
            return "UserReplayCardInfoList{" +
                    "id=" + id +
                    ", content='" + content + '\'' +
                    ", images=" + images +
                    ", created_at='" + created_at + '\'' +
                    ", floor_count=" + floor_count +
                    ", bangumi=" + bangumi +
                    ", post=" + post +
                    '}';
        }
    }
    public class UserReplayCardInfoImage{
        private String url;
        private String width;
        private String height;
        private String size;
        private String type;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "UserReplayCardInfoImage{" +
                    "url='" + url + '\'' +
                    ", width='" + width + '\'' +
                    ", height='" + height + '\'' +
                    ", size='" + size + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
    public class UserReplayCardInfoBangumi{
        private int id;
        private String name;
        private String avatar;

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

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        @Override
        public String toString() {
            return "UserReplayCardInfoBangumi{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    '}';
        }
    }
    public class UserReplayCardInfoPost{
        private int id;
        private String title;
        private String content;
        private List<UserReplayCardInfoImage> images;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<UserReplayCardInfoImage> getImages() {
            return images;
        }

        public void setImages(List<UserReplayCardInfoImage> images) {
            this.images = images;
        }

        @Override
        public String toString() {
            return "UserReplayCardInfoPost{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", images=" + images +
                    '}';
        }
    }
}
