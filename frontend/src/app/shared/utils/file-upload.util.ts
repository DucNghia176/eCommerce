export function handleImagesSelected(
  event: Event,
  callback: (images: { src: string; file: File }[]) => void,
  showError: (msg: string) => void
): void {
  const input = event.target as HTMLInputElement;
  if (!input.files || input.files.length === 0) return;

  const files = Array.from(input.files);
  const images: { src: string; file: File }[] = [];

  files.forEach((file) => {
    if (!file.type.startsWith('image/')) {
      showError("Chỉ được phép chọn tệp ảnh (jpg, png, gif...)");
      input.value = '';
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      images.push({src: reader.result as string, file});
      // Khi đã đọc xong hết thì mới callback
      if (images.length === files.length) {
        callback(images);
      }
    };
    reader.readAsDataURL(file);
  });

  input.value = '';
}
