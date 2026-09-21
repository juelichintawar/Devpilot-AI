package com.example.devpilotai.ui.ocr;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.devpilotai.R;
import com.example.devpilotai.data.OcrLogRepository;
import com.example.devpilotai.data.StatsRepository;
import com.example.devpilotai.data.model.OcrLog;
import com.example.devpilotai.databinding.FragmentOcrBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class OcrFragment extends Fragment {

    private FragmentOcrBinding binding;
    private TextRecognizer textRecognizer;
    private StatsRepository statsRepository;
    private OcrLogRepository ocrLogRepository;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Void> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
                if (bitmap != null) {
                    binding.ivSelectedImage.setImageBitmap(bitmap);
                    recognizeText(InputImage.fromBitmap(bitmap, 0));
                }
            });

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                        binding.ivSelectedImage.setImageBitmap(bitmap);
                        recognizeText(InputImage.fromFilePath(requireContext(), uri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOcrBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        statsRepository = new StatsRepository(requireActivity().getApplication());
        ocrLogRepository = new OcrLogRepository();

        binding.btnCamera.setOnClickListener(v -> checkCameraPermission());
        binding.btnGallery.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        binding.chipCopy.setOnClickListener(v -> {
            String text = binding.etExtractedText.getText().toString();
            if (!text.isEmpty()) {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Extracted Text", text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        binding.chipShare.setOnClickListener(v -> {
            String text = binding.etExtractedText.getText().toString();
            if (!text.isEmpty()) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, null));
            }
        });

        binding.chipSendAI.setOnClickListener(v -> {
            String text = binding.etExtractedText.getText().toString();
            if (!text.isEmpty()) {
                Bundle args = new Bundle();
                args.putString("initial_text", "Explain this code or text:\n" + text);
                Navigation.findNavController(requireView()).navigate(R.id.action_ocrFragment_to_chatFragment, args);
            }
        });

        binding.chipSaveSnippet.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Snippet saved (Feature coming soon!)", Toast.LENGTH_SHORT).show();
        });
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        takePictureLauncher.launch(null);
    }

    private void recognizeText(InputImage image) {
        binding.processingIndicator.setVisibility(View.VISIBLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = (user != null) ? user.getEmail() : "Anonymous";

        textRecognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    binding.processingIndicator.setVisibility(View.GONE);
                    String text = visionText.getText();
                    binding.etExtractedText.setText(text);
                    if (!text.isEmpty()) {
                        statsRepository.incrementOcrScans();
                        String snippet = text.length() > 100 ? text.substring(0, 100) + "..." : text;
                        ocrLogRepository.logOcr(new OcrLog(userEmail, "SUCCESS", null, System.currentTimeMillis(), snippet));
                    } else {
                         ocrLogRepository.logOcr(new OcrLog(userEmail, "SUCCESS", "No text found", System.currentTimeMillis(), ""));
                    }
                })
                .addOnFailureListener(e -> {
                    binding.processingIndicator.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Failed to recognize text: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    ocrLogRepository.logOcr(new OcrLog(userEmail, "FAILED", e.getMessage(), System.currentTimeMillis(), null));
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
