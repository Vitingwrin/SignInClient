package com.hellochiu.soap;

import android.util.Log;

import com.hellochiu.model.Course;
import com.hellochiu.model.CourseStuDetail;
import com.hellochiu.model.Student;
import com.hellochiu.model.Teacher;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class XmlParser {

    private static final String TAG = "XmlParser";
    
    public static void parseStuInfo(String xml) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("stuName".equals(nodeName)) {
                            Student.CURRENT_USER.setStuName(parser.nextText());
                        } else if ("stuEmail".equals(nodeName)) {
                            Student.CURRENT_USER.setStuEmail(parser.nextText());
                        } else if ("stuPhone".equals(nodeName)) {
                            Student.CURRENT_USER.setStuPhone(parser.nextText());
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseTchInfo(String xml) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("tchName".equals(nodeName)) {
                            Teacher.CURRENT_USER.setTchName(parser.nextText());
                        } else if ("tchEmail".equals(nodeName)) {
                            Teacher.CURRENT_USER.setTchEmail(parser.nextText());
                        } else if ("tchPhone".equals(nodeName)) {
                            Teacher.CURRENT_USER.setTchPhone(parser.nextText());
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseStuSelectedCourse(String xml) {

        Course.SELECTED_COURSE.clear();
        if (CourseService.NONE.equals(xml)) {
            return;
        }
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            Course course = new Course();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("beginTime".equals(nodeName)) {
                            course.setBeginTime(parser.nextText());
                        } else if ("endTime".equals(nodeName)) {
                            course.setEndTime(parser.nextText());
                        } else if ("courseName".equals(nodeName)) {
                            course.setCourseName(parser.nextText());
                        } else if ("tchName".equals(nodeName)) {
                            course.setTchName(parser.nextText());
                        } else if ("courseId".equals(nodeName)) {
                            course.setCourseId(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("Course".equals(nodeName)) {
                            Course.SELECTED_COURSE.add(course);
                            course = new Course();
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseTchCreatedCourse(String xml) {

        Course.CREATED_COURSE.clear();
        if (CourseService.NONE.equals(xml)) {
            return;
        }
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            Course course = new Course();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("beginTime".equals(nodeName)) {
                            course.setBeginTime(parser.nextText());
                        } else if ("endTime".equals(nodeName)) {
                            course.setEndTime(parser.nextText());
                        } else if ("courseName".equals(nodeName)) {
                            course.setCourseName(parser.nextText());
                        } else if ("tchId".equals(nodeName)) {
                            course.setTchId(parser.nextText());
                        } else if ("courseId".equals(nodeName)) {
                            course.setCourseId(parser.nextText());
                        } else if ("coursePwd".equals(nodeName)) {
                            course.setCoursePwd(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("Course".equals(nodeName)) {
                            Course.CREATED_COURSE.add(course);
                            course = new Course();
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseAllCourse(String xml) {

        Course.ALL_COURSE.clear();
        if (CourseService.NONE.equals(xml)) {
            return;
        }
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            Course course = new Course();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("beginTime".equals(nodeName)) {
                            course.setBeginTime(parser.nextText());
                        } else if ("endTime".equals(nodeName)) {
                            course.setEndTime(parser.nextText());
                        } else if ("courseName".equals(nodeName)) {
                            course.setCourseName(parser.nextText());
                        } else if ("tchId".equals(nodeName)) {
                            course.setTchId(parser.nextText());
                        } else if ("courseId".equals(nodeName)) {
                            course.setCourseId(parser.nextText());
                        } else if ("coursePwd".equals(nodeName)) {
                            course.setCoursePwd(parser.nextText());
                        } else if ("tchName".equals(nodeName)) {
                            course.setTchName(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("Course".equals(nodeName)) {
                            Course.ALL_COURSE.add(course);
                            course = new Course();
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }


    public static void parseCourseStuDetail(String xml, List<CourseStuDetail> details) {

        details.clear();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            CourseStuDetail detail = new CourseStuDetail();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("stuId".equals(nodeName)) {
                            detail.setStuId(parser.nextText());
                        } else if ("stuName".equals(nodeName)) {
                            detail.setStuName(parser.nextText());
                        } else if ("stuEmail".equals(nodeName)) {
                            detail.setStuEmail(parser.nextText());
                        } else if ("stuPhone".equals(nodeName)) {
                            detail.setStuPhone(parser.nextText());
                        } else if ("rate".equals(nodeName)) {
                            detail.setRate(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("CourseStuDetail".equals(nodeName)) {
                            details.add(detail);
                            detail = new CourseStuDetail();
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

}
