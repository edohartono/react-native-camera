package org.reactnative.facedetector;

import android.graphics.PointF;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;

import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceLandmark;
import com.google.mlkit.vision.face.FaceContour;
import java.util.*;

public class FaceDetectorUtils {
  private static final String[] landmarkNames = {
          "bottomMouthPosition", "leftCheekPosition", "leftEarPosition",
          "leftEyePosition", "leftMouthPosition", "noseBasePosition", "rightCheekPosition",
          "rightEarPosition", "rightEyePosition", "rightMouthPosition"
  };

  private static final String[] contourNames = {
           "face", "leftEye", "leftEyebrowBottom", "leftEyebrowTop", "lowerLipBottom", "lowerLipTop", "noseBottom", "noseBridge", "rightEye", "rightEyebrowBottom", "rightEyebrowTop", "upperLipBottom", "upperLipTop"
  };

  private static final int[] contourTypes = {
    // FaceContour.ALL_POINTS,
    FaceContour.FACE,
    FaceContour.LEFT_EYE,
    FaceContour.LEFT_EYEBROW_BOTTOM,
    FaceContour.LEFT_EYEBROW_TOP,
    FaceContour.LOWER_LIP_BOTTOM,
    FaceContour.LOWER_LIP_TOP,
    FaceContour.NOSE_BOTTOM,
    FaceContour.NOSE_BRIDGE,
    FaceContour.RIGHT_EYE,
    FaceContour.RIGHT_EYEBROW_BOTTOM,
    FaceContour.RIGHT_EYEBROW_TOP,
    FaceContour.UPPER_LIP_BOTTOM,
    FaceContour.UPPER_LIP_TOP,
  };

  public static WritableMap serializeFace(Face face) {
    return serializeFace(face, 1, 1, 0, 0, 0, 0);
  }

  public static WritableMap serializeFace(Face face, double scaleX, double scaleY, int width, int height, int paddingLeft, int paddingTop) {
    WritableMap encodedFace = Arguments.createMap();

    int id = 0;
    // If face tracking was enabled:
    if (face.getTrackingId() != null) {
      id = face.getTrackingId();
    }


    encodedFace.putInt("faceID", id);
    encodedFace.putDouble("rollAngle", face.getHeadEulerAngleZ());
    encodedFace.putDouble("yawAngle", face.getHeadEulerAngleY());

    // If classification was enabled:
    if (face.getSmilingProbability() != null) {
      encodedFace.putDouble("smilingProbability", face.getSmilingProbability());
    }
    if (face.getLeftEyeOpenProbability() != null) {
      encodedFace.putDouble("leftEyeOpenProbability", face.getLeftEyeOpenProbability());
    }
    if (face.getRightEyeOpenProbability() != null) {
      encodedFace.putDouble("rightEyeOpenProbability", face.getRightEyeOpenProbability());
    }
    int[] landmarks = {
            FaceLandmark.MOUTH_BOTTOM,
            FaceLandmark.LEFT_CHEEK,
            FaceLandmark.LEFT_EAR,
            FaceLandmark.LEFT_EYE,
            FaceLandmark.MOUTH_LEFT,
            FaceLandmark.NOSE_BASE,
            FaceLandmark.RIGHT_CHEEK,
            FaceLandmark.RIGHT_EAR,
            FaceLandmark.RIGHT_EYE,
            FaceLandmark.MOUTH_RIGHT
    };

    for (int i = 0; i < landmarks.length; ++i) {
      FaceLandmark landmark = face.getLandmark(landmarks[i]);
      if (landmark != null) {
        encodedFace.putMap(landmarkNames[i], mapFromPoint(landmark.getPosition(), scaleX, scaleY, width, height, paddingLeft, paddingTop));
      }
    }

    // if (!face.getContour(FaceContour.ALL_POINTS).getPoints().isEmpty()) {
      WritableMap contours = Arguments.createMap();
      // WritableMap testContours = Arguments.createMap();
      // List<FaceContour> allFaceContour  = face.getAllContours();
      
      // for (int i = 0; i < contourTypes.length; ++i) {
      //   FaceContour contourPoints = allFaceContour.get(contourTypes[i]).getPoints();
      //   System.out.println(contourPoints);
      //   WritableArray points = Arguments.createArray();
      //   if (contourPoints != null) {
      //     for (int j = 0; j < contourPoints.size(); ++j) {
      //       points.pushMap(mapFromPoint(contourPoints.get(j), scaleX, scaleY, width, height, paddingLeft, paddingTop));
      //     }
      //     contours.putArray(contourNames[i], points);

      //   }
      // }

      WritableMap testContours = new WritableNativeMap();

      // WritableMap rawContours = new WritableNativeMap();
      // rawContours.putArray(face.getAllContours());
      List <FaceContour> rawContours = face.getAllContours();

      for (int i = 0; i < contourTypes.length; i++) {
        FaceContour contour = face.getContour(contourTypes[i]);
        if (contour != null){
          List<PointF> points = contour.getPoints();
          WritableNativeArray pointsArray = new WritableNativeArray();

          for (int j = 0; j < points.size(); j++) {
            WritableMap currentPointsMap = new WritableNativeMap();

            currentPointsMap.putDouble("x", points.get(j).x);
            currentPointsMap.putDouble("y", points.get(j).y);

            pointsArray.pushMap(currentPointsMap);
          }
          testContours.putArray(contourNames[i], pointsArray);

        }
      }

    // return faceContoursTypesMap;


      for (int i = 0; i < contourTypes.length; i++) {
        FaceContour contour = face.getContour(contourTypes[i]);
        if (contour != null) {

          List<PointF> points = contour.getPoints();
          WritableArray pointsArray = Arguments.createArray();

            for (int j = 0; j < points.size(); j++) {
              WritableArray currentPointsMap = Arguments.createArray();

              // currentPointsMap.putDouble("x", points.get(j).x);
              // currentPointsMap.putDouble("y", points.get(j).y);

              // pointsArray.pushMap(currentPointsMap);
              pointsArray.pushMap(mapFromPoint(points.get(j), scaleX, scaleY, width, height, paddingLeft, paddingTop));
            }
            // contours.putArray(faceContoursTypesStrings[contour.getFaceContourType() - 1], pointsArray);
            contours.putArray(contourNames[i], pointsArray);
            // testContours.putArray(contourNames[i], pointsArray);
        }

      }

      encodedFace.putMap("contours", contours);
      encodedFace.putMap("old_contour", testContours);
      encodedFace.putInt("raw_contours", rawContours.size());
    // }

    WritableMap origin = Arguments.createMap();
    Float x = face.getBoundingBox().exactCenterX() - (face.getBoundingBox().width() / 2 );
    Float y = face.getBoundingBox().exactCenterY() - (face.getBoundingBox().height() / 2);
    if (face.getBoundingBox().exactCenterX() < width / 2) {
      x = x + paddingLeft / 2;
    } else if (face.getBoundingBox().exactCenterX() > width / 2) {
      x = x - paddingLeft / 2;
    }

    if (face.getBoundingBox().exactCenterY() < height / 2) {
      y = y + paddingTop / 2;
    } else if (face.getBoundingBox().exactCenterY() > height / 2) {
      y = y - paddingTop / 2;
    }

    origin.putDouble("x", x * scaleX);
    origin.putDouble("y", y * scaleY);

    WritableMap size = Arguments.createMap();
    size.putDouble("width", face.getBoundingBox().width() * scaleX);
    size.putDouble("height", face.getBoundingBox().height() * scaleY);

    WritableMap bounds = Arguments.createMap();
    bounds.putMap("origin", origin);
    bounds.putMap("size", size);
    

    encodedFace.putMap("bounds", bounds);

    return encodedFace;
  }

  public static WritableMap rotateFaceX(WritableMap face, int sourceWidth, double scaleX) {
    ReadableMap faceBounds = face.getMap("bounds");

    ReadableMap oldOrigin = faceBounds.getMap("origin");
    WritableMap mirroredOrigin = positionMirroredHorizontally(oldOrigin, sourceWidth, scaleX);

    double translateX = -faceBounds.getMap("size").getDouble("width");
    WritableMap translatedMirroredOrigin = positionTranslatedHorizontally(mirroredOrigin, translateX);

    WritableMap newBounds = Arguments.createMap();
    newBounds.merge(faceBounds);
    newBounds.putMap("origin", translatedMirroredOrigin);

    for (String landmarkName : landmarkNames) {
      ReadableMap landmark = face.hasKey(landmarkName) ? face.getMap(landmarkName) : null;
      if (landmark != null) {
        WritableMap mirroredPosition = positionMirroredHorizontally(landmark, sourceWidth, scaleX);
        face.putMap(landmarkName, mirroredPosition);
      }
    }

    ReadableMap contours = face.hasKey("contours") ? face.getMap("contours") : null;
    if (contours != null) {
      WritableMap newContours = Arguments.createMap();
      for (String contourName : contourNames) {
        if (contours.getArray(contourName) != null) {
          ReadableArray contourPoints = contours.getArray(contourName);
          WritableArray mirroredContourPoints = Arguments.createArray();
          for (int j = 0; j < contourPoints.size(); ++j) {
            mirroredContourPoints.pushMap(positionMirroredHorizontally(contourPoints.getMap(j), sourceWidth, scaleX));
          }
          newContours.putArray(contourName, mirroredContourPoints);

        }
      }
      face.putMap("contours", newContours);
    }

    face.putMap("bounds", newBounds);

    return face;
  }

  public static WritableMap changeAnglesDirection(WritableMap face) {
    face.putDouble("rollAngle", (-face.getDouble("rollAngle") + 360) % 360);
    face.putDouble("yawAngle", (-face.getDouble("yawAngle") + 360) % 360);
    return face;
  }

  public static WritableMap mapFromPoint(PointF point, double scaleX, double scaleY, int width, int height, int paddingLeft, int paddingTop) {
    WritableMap map = Arguments.createMap();
    Float x = point.x;
    Float y = point.y;
    if (point.x < width / 2) {
      x = (x + paddingLeft / 2);
    } else if (point.x > width / 2) {
      x = (x - paddingLeft / 2);
    }

    if (point.y < height / 2) {
      y = (y + paddingTop / 2);
    } else if (point.y > height / 2) {
      y = (y - paddingTop / 2);
    }
    map.putDouble("x", x * scaleX);
    map.putDouble("y", y * scaleY);
    return map;
  }

  public static WritableMap positionTranslatedHorizontally(ReadableMap position, double translateX) {
    WritableMap newPosition = Arguments.createMap();
    newPosition.merge(position);
    newPosition.putDouble("x", position.getDouble("x") + translateX);
    return newPosition;
  }

  public static WritableMap positionMirroredHorizontally(ReadableMap position, int containerWidth, double scaleX) {
    WritableMap newPosition = Arguments.createMap();
    newPosition.merge(position);
    newPosition.putDouble("x", valueMirroredHorizontally(position.getDouble("x"), containerWidth, scaleX));
    return newPosition;
  }

  public static double valueMirroredHorizontally(double elementX, int containerWidth, double scaleX) {
    double originalX = elementX / scaleX;
    double mirroredX = containerWidth - originalX;
    return mirroredX * scaleX;
  }
}
