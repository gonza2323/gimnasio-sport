package ar.edu.uncuyo.gimnasio_sport.error;

//@ControllerAdvice
//@RequiredArgsConstructor
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(FieldSpecificBusinessException.class)
//    public String handleFieldSpecific(FieldSpecificBusinessException ex,
//                                      @Nullable BindingResult bindingResult) {
//        if (bindingResult != null) {
//            bindingResult.rejectValue(ex.getField(), ex.getMessageKey(), "Ocurrió un error");
//        }
//        return ex.getViewName();
//    }
//
//    @ExceptionHandler(BusinessException.class)
//    public String handleGlobal(BusinessException ex,
//                               Model model) {
//        model.addAttribute("msgError", "Error de sistema");
//        return ex.getViewName();
//    }
//}